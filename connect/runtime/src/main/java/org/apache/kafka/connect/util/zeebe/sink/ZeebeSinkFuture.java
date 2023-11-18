package org.apache.kafka.connect.util.zeebe.sink;/*
 * Copyright © 2019 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ZeebeSinkFuture extends CompletableFuture<PublishMessageResponse> {

  public static final Set<Code> SUCCESS_CODES = EnumSet.of(Code.OK, Code.ALREADY_EXISTS);
  public static final Set<Code> RETRIABLE_CODES =
      EnumSet.of(
          Code.CANCELLED,
          Code.DEADLINE_EXCEEDED,
          Code.RESOURCE_EXHAUSTED,
          Code.ABORTED,
          Code.UNAVAILABLE,
          Code.DATA_LOSS);
  public static final Set<Code> FAILURE_CODES =
      EnumSet.of(
          Code.INVALID_ARGUMENT,
          Code.NOT_FOUND,
          Code.PERMISSION_DENIED,
          Code.FAILED_PRECONDITION,
          Code.OUT_OF_RANGE,
          Code.UNIMPLEMENTED,
          Code.INTERNAL,
          Code.UNAUTHENTICATED);

  private static final Logger LOGGER = LoggerFactory.getLogger(ZeebeSinkFuture.class);

  private final FinalCommandStep<PublishMessageResponse> command;

  // TODO inject reusable executor
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private BackoffSupplier backoffSupplier;
  private long currentRetryDelay = 50L;

  ZeebeSinkFuture(
      final FinalCommandStep<PublishMessageResponse> command, BackoffSupplier backoffSupplier) {
    this.command = command;
    this.backoffSupplier = backoffSupplier;
  }

  @SuppressWarnings("unchecked")
  private CompletableFuture<PublishMessageResponse> sendCommand() {
    return (CompletableFuture<PublishMessageResponse>) command.send();
  }

  CompletableFuture<PublishMessageResponse> executeAsync() {
    sendCommand()
        .whenCompleteAsync(
            (aVoid, throwable) -> {
              if (throwable == null) {
                this.complete(aVoid);
              } else if (throwable instanceof StatusRuntimeException) {
                // handle gRPC errors
                final StatusRuntimeException statusException = (StatusRuntimeException) throwable;
                final Code code = statusException.getStatus().getCode();
                if (SUCCESS_CODES.contains(code)) {
                  complete(null);
                } else if (RETRIABLE_CODES.contains(code)) {
                  currentRetryDelay = backoffSupplier.supplyRetryDelay(currentRetryDelay);
                  LOGGER.trace(
                      "Retry "
                          + command
                          + " after seeing "
                          + code
                          + ", backoff/delay: "
                          + currentRetryDelay
                          + " ms");
                  executor.schedule(this::executeAsync, currentRetryDelay, TimeUnit.MILLISECONDS);
                } else if (FAILURE_CODES.contains(code)) {
                  completeExceptionally(throwable);
                } else {
                  LOGGER.warn("Unexpected gRPC status code {} received", code, throwable);
                  completeExceptionally(throwable);
                }
              } else {
                completeExceptionally(throwable);
              }
            });

    return this;
  }
}
