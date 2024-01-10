/*% if (feature.T_P_RedSys) { %*/
package es.udc.lbd.gema.lps.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redsys", ignoreUnknownFields = false)
public class RedSysConfiguration {
  private String currency;
  private String key;
  private String merchantCode;
  private String publicClientUrl;
  private String publicServerUrl;
  private String terminal;

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getMerchantCode() {
    return merchantCode;
  }

  public void setMerchantCode(String merchantCode) {
    this.merchantCode = merchantCode;
  }

  public String getPublicClientUrl() {
    return publicClientUrl;
  }

  public void setPublicClientUrl(String publicClientUrl) {
    this.publicClientUrl = publicClientUrl;
  }

  public String getPublicServerUrl() {
    return publicServerUrl;
  }

  public void setPublicServerUrl(String publicServerUrl) {
    this.publicServerUrl = publicServerUrl;
  }

  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(String terminal) {
    this.terminal = terminal;
  }
}
/*% } %*/
