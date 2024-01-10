/*% if (feature.T_P_PayPal) { %*/
package es.udc.lbd.gema.lps.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "paypal", ignoreUnknownFields = false)
public class PaypalConfiguration {
  private String clientId;
  private String secret;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
/*% } %*/
