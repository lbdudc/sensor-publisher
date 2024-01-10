/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(prefix = "security-properties", ignoreUnknownFields = false)
public class SecurityProperties {

    private final Security security = new Security();

    public Security getSecurity() {
        return security;
    }

    public static class Security {
         /*% if (feature.UM_ST_JWT) { %*/
            private String jwtSecretKey;
            private Long jwtValidity;
            /*% if (feature.UM_A_RememberPass) { %*/
            private Long shortJwtValidity;
            /*% } %*/

            public String getJwtSecretKey() {
                return jwtSecretKey;
            }

            public void setJwtSecretKey(String jwtSecretKey) {
                this.jwtSecretKey = jwtSecretKey;
            }

            public Long getJwtValidity() {
                return jwtValidity;
            }

            public void setJwtValidity(Long jwtValidity) {
                this.jwtValidity = jwtValidity;
            }

            /*% if (feature.UM_A_RememberPass) { %*/
            public Long getShortJwtValidity() {
              return shortJwtValidity;
            }

            public void setShortJwtValidity(Long shortJwtValidity) {
              this.shortJwtValidity = shortJwtValidity;
            }
            /*% } %*/
         /*% } %*/
    }
}
/*% } %*/
