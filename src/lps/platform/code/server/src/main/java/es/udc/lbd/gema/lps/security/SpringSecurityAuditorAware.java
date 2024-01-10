/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import es.udc.lbd.gema.lps.config.Constants;

/** Implementation of AuditorAware based on Spring Security. */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    String userName = SecurityUtils.getCurrentUserLogin();
    return userName != null ? Optional.of(userName) : Optional.of(Constants.SYSTEM_ACCOUNT);
  }
}
/*% } %*/
