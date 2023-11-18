package org.apache.kafka.connect.util.zeebe.util;


import io.camunda.zeebe.client.ClientProperties;
import java.time.Duration;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;

public final class ZeebeClientConfigDef {

  public static final String GATEWAY_ADDRESS_CONFIG = ClientProperties.GATEWAY_ADDRESS;
  public static final String REQUEST_TIMEOUT_CONFIG = ClientProperties.DEFAULT_REQUEST_TIMEOUT;
  public static final String USE_PLAINTEXT_CONFIG = ClientProperties.USE_PLAINTEXT_CONNECTION;

  public static final String CAMUNDA_CLOUD_CLUSTER_ID_CONFIG = "zeebe.client.cloud.clusterId";
  public static final String CAMUNDA_CLOUD_REGION_CONFIG = "zeebe.client.cloud.region";
  public static final String CAMUNDA_CLOUD_CLIENT_ID_CONFIG = "zeebe.client.cloud.clientId";
  public static final String CAMUNDA_CLOUD_CLIENT_SECRET_CONFIG = "zeebe.client.cloud.clientSecret";
  public static final String ZEEBE_TOKEN_AUDIENCE_CONFIG = "zeebe.client.cloud.token.audience";
  public static final String ZEEBE_AUTHORIZATION_SERVER_URL_CONFIG =
      "zeebe.client.cloud.authorization.server.url";

  private static final String CLIENT_CONFIG_GROUP = "Zeebe Client";
  private static final String GATEWAY_ADDRESS_DEFAULT = "localhost:26500";
  private static final String GATEWAY_ADDRESS_DOC =
      "Gateway address, e.g. ``localhost:26500``, for the Zeebe client";
  private static final long REQUEST_TIMEOUT_DEFAULT = Duration.ofSeconds(1).toMillis();
  private static final String REQUEST_TIMEOUT_DOC =
      "How long to wait before a request to the broker is timed out";
  private static final boolean USE_PLAINTEXT_DEFAULT = false;
  private static final String USE_PLAINTEXT_DOC =
      "Disable secure connection to gateway for the Zeebe client";

  private static final String CAMUNDA_CLOUD_CLUSTER_ID_DOC =
      "Camunda SaaS Cluster ID to connect to (on cloud.camunda.io). If set this is used instead of the gateway address.";
  private static final String CAMUNDA_CLOUD_REGION_DOC =
      "Camunda SaaS Region the cluster is provisioned in";
  private static final String CAMUNDA_CLOUD_CLIENT_ID_DOC = "Camunda SaaS Client ID";
  private static final String CAMUNDA_CLOUD_CLIENT_SECRET_DOC = "Camunda SaaS Client Secret";
  private static final String ZEEBE_TOKEN_AUDIENCE_DOC =
      "The address for which the authorization server token should be valid";
  private static final String ZEEBE_AUTHORIZATION_SERVER_URL_DOC =
      "The URL of the authorization server from which the access token will be requested (by default, configured for Camunda SaaS)";
  private static final String ZEEBE_CLIENT_CONFIG_PATH_DOC =
      "The path to a cache file where the access tokens will be stored (by default, it's $HOME/.camunda/credentials)";

  private ZeebeClientConfigDef() {}

  public static void defineClientGroup(final ConfigDef definitions) {
    int order = 0;

    definitions
        .define(
            GATEWAY_ADDRESS_CONFIG,
            Type.STRING,
            GATEWAY_ADDRESS_DEFAULT,
            Importance.HIGH,
            GATEWAY_ADDRESS_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.SHORT,
            "Broker contact point")
        .define(
            REQUEST_TIMEOUT_CONFIG,
            Type.LONG,
            REQUEST_TIMEOUT_DEFAULT,
            Importance.LOW,
            REQUEST_TIMEOUT_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.SHORT,
            "Request timeout")
        .define(
            USE_PLAINTEXT_CONFIG,
            Type.BOOLEAN,
            USE_PLAINTEXT_DEFAULT,
            Importance.LOW,
            USE_PLAINTEXT_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.SHORT,
            "Use plaintext connection")
        .define(
            CAMUNDA_CLOUD_CLUSTER_ID_CONFIG,
            Type.STRING,
            null,
            Importance.LOW,
            CAMUNDA_CLOUD_CLUSTER_ID_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.MEDIUM,
            "Zeebe Cluster Id")
        .define(
            CAMUNDA_CLOUD_REGION_CONFIG,
            Type.STRING,
            null,
            Importance.LOW,
            CAMUNDA_CLOUD_REGION_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.MEDIUM,
            "Zeebe Cluster Region")
        .define(
            CAMUNDA_CLOUD_CLIENT_ID_CONFIG,
            Type.STRING,
            null,
            Importance.LOW,
            CAMUNDA_CLOUD_CLIENT_ID_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.MEDIUM,
            "Zeebe Client Id")
        .define(
            CAMUNDA_CLOUD_CLIENT_SECRET_CONFIG,
            Type.STRING,
            null,
            Importance.LOW,
            CAMUNDA_CLOUD_CLIENT_SECRET_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.MEDIUM,
            "Zeebe Client Secret")
        .define(
            ZEEBE_TOKEN_AUDIENCE_CONFIG,
            Type.STRING,
            null,
            Importance.LOW,
            ZEEBE_TOKEN_AUDIENCE_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.MEDIUM,
            "Zeebe Token Audience")
        .define(
            ZEEBE_AUTHORIZATION_SERVER_URL_CONFIG,
            Type.STRING,
            null,
            Importance.LOW,
            ZEEBE_AUTHORIZATION_SERVER_URL_DOC,
            CLIENT_CONFIG_GROUP,
            ++order,
            Width.MEDIUM,
            "Zeebe Authorization Server URL");
  }
}
