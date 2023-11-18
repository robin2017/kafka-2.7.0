package org.apache.kafka.connect.util.zeebe.sink;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.command.PublishMessageCommandStep1.PublishMessageCommandStep3;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import io.camunda.zeebe.client.api.worker.BackoffSupplier;
import io.camunda.zeebe.client.impl.worker.ExponentialBackoffBuilderImpl;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.util.zeebe.sink.message.JsonRecordParser;
import org.apache.kafka.connect.util.zeebe.sink.message.Message;
import org.apache.kafka.connect.util.zeebe.util.ManagedClient;
import org.apache.kafka.connect.util.zeebe.util.VersionInfo;
import org.apache.kafka.connect.util.zeebe.util.ZeebeClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZeebeSinkTask extends SinkTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZeebeSinkTask.class);

  private ManagedClient managedClient;
  private JsonRecordParser parser;
  private BackoffSupplier backoffSupplier;

  @Override
  public void start(final Map<String, String> props) {
    final ZeebeSinkConnectorConfig config = new ZeebeSinkConnectorConfig(props);
    managedClient = new ManagedClient(ZeebeClientHelper.buildClient(config));
    parser = buildParser(config);
    backoffSupplier =
        new ExponentialBackoffBuilderImpl()
            .maxDelay(1000L)
            .minDelay(50L)
            .backoffFactor(1.5)
            .jitterFactor(0.2)
            .build();
  }

  // The documentation specifies that we probably shouldn't block here but I'm not sure what the
  // consequences of doing so are so for now we await all futures
  @Override
  public void put(final Collection<SinkRecord> sinkRecords) {
    try {
      managedClient.withClient(client -> publishMessages(client, sinkRecords).join());
      LOGGER.trace("Published {} messages", sinkRecords.size());
    } catch (final CancellationException e) {
      LOGGER.debug("Publish requests cancelled, probably due to task stopping", e);
    } catch (final ManagedClient.AlreadyClosedException e) {
      LOGGER.debug(
          "Expected to publish {} messages, but the client is already closed", sinkRecords.size());
    } catch (final Exception e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void stop() {
    managedClient.close();
  }

  @Override
  public String version() {
    return VersionInfo.getVersion();
  }

  private CompletableFuture<Void> publishMessages(
      final ZeebeClient client, final Collection<SinkRecord> sinkRecords) {
    final CompletableFuture[] inFlightRequests =
        sinkRecords.stream()
            .map(r -> this.preparePublishRequest(client, r))
            .map(command -> new ZeebeSinkFuture(command, backoffSupplier))
            .map(ZeebeSinkFuture::executeAsync)
            .toArray(CompletableFuture[]::new);

    return CompletableFuture.allOf(inFlightRequests);
  }

  private FinalCommandStep<PublishMessageResponse> preparePublishRequest(
      final ZeebeClient client, final SinkRecord record) {
    final Message message = parser.parse(record);
    PublishMessageCommandStep3 request =
        client
            .newPublishMessageCommand()
            .messageName(message.getName())
            .correlationKey(message.getKey())
            .messageId(message.getId());

    if (message.hasTimeToLive()) {
      request = request.timeToLive(message.getTimeToLive());
    }

    if (message.hasVariables()) {
      request = request.variables(message.getVariables());
    }

    LOGGER.debug("Publishing message {}", message);
    return request;
  }

  private JsonRecordParser buildParser(final ZeebeSinkConnectorConfig config) {
    final JsonRecordParser.Builder builder =
        JsonRecordParser.builder()
            .withKeyPath(config.getString(ZeebeSinkConnectorConfig.MESSAGE_PATH_KEY_CONFIG))
            .withNamePath(config.getString(ZeebeSinkConnectorConfig.MESSAGE_PATH_NAME_CONFIG));

    final String ttlPath = config.getString(ZeebeSinkConnectorConfig.MESSAGE_PATH_TTL_CONFIG);
    if (ttlPath != null && !ttlPath.isEmpty()) {
      builder.withTtlPath(ttlPath);
    }

    final String variablesPath =
        config.getString(ZeebeSinkConnectorConfig.MESSAGE_PATH_VARIABLES_CONFIG);
    if (variablesPath != null && !variablesPath.isEmpty()) {
      builder.withVariablesPath(variablesPath);
    }

    return builder.build();
  }
}
