/*
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
package org.apache.kafka.connect.util.zeebe.util;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.ZeebeClientCloudBuilderStep1.ZeebeClientCloudBuilderStep2.ZeebeClientCloudBuilderStep3.ZeebeClientCloudBuilderStep4;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import java.time.Duration;
import org.apache.kafka.common.config.AbstractConfig;

public class ZeebeClientHelper {

  public static ZeebeClient buildClient(final AbstractConfig config) {
    final long requestTimeoutMs = config.getLong(ZeebeClientConfigDef.REQUEST_TIMEOUT_CONFIG);

    final String camundaCloudClusterId =
        config.getString(ZeebeClientConfigDef.CAMUNDA_CLOUD_CLUSTER_ID_CONFIG);
    final String camundaCloudClientId =
        config.getString(ZeebeClientConfigDef.CAMUNDA_CLOUD_CLIENT_ID_CONFIG);
    final String camundaCloudClientSecret =
        config.getString(ZeebeClientConfigDef.CAMUNDA_CLOUD_CLIENT_SECRET_CONFIG);
    final String camundaCloudRegion =
        config.getString(ZeebeClientConfigDef.CAMUNDA_CLOUD_REGION_CONFIG);

    if (camundaCloudClusterId != null) {
      final ZeebeClientCloudBuilderStep4 builder =
          ZeebeClient.newCloudClientBuilder()
              .withClusterId(camundaCloudClusterId)
              .withClientId(camundaCloudClientId)
              .withClientSecret(camundaCloudClientSecret);

      if (camundaCloudRegion != null) {
        builder.withRegion(camundaCloudRegion);
      }
      return builder.build();
    } else {
      // Zeebe directly (e.g. self-managed or localhost)
      final ZeebeClientBuilder zeebeClientBuilder =
          ZeebeClient.newClientBuilder()
              .gatewayAddress(config.getString(ZeebeClientConfigDef.GATEWAY_ADDRESS_CONFIG))
              .numJobWorkerExecutionThreads(1)
              .defaultRequestTimeout(Duration.ofMillis(requestTimeoutMs));

      if (config.getBoolean(ZeebeClientConfigDef.USE_PLAINTEXT_CONFIG)) {
        zeebeClientBuilder.usePlaintext();
      }

      if (camundaCloudClientId != null) {
        final String tokenAudience =
            config.getString(ZeebeClientConfigDef.ZEEBE_TOKEN_AUDIENCE_CONFIG);

        final OAuthCredentialsProvider provider =
            new OAuthCredentialsProviderBuilder()
                .clientId(camundaCloudClientId)
                .clientSecret(camundaCloudClientSecret)
                .audience(tokenAudience)
                .build();
        zeebeClientBuilder.credentialsProvider(provider);
      }

      return zeebeClientBuilder.build();
    }
  }
}
