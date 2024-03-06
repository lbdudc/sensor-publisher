/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.component.scheduled_jobs;

import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.repository.user_management.UMUserRepository;
import es.udc.lbd.gema.lps.model.service.UMUserService;
import es.udc.lbd.gema.lps.model.service.exceptions.account.AccountException;
import es.udc.lbd.gema.lps.security.AuthoritiesConstants;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
  /*% if (feature.T_Quartz) { %*/
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
  /*% } else { %*/
import org.springframework.stereotype.Component;
  /*% } %*/

/*% if (!feature.T_Quartz) { %*/@Component/*% } %*/
public class InitDBJob /*% if (feature.T_Quartz) { %*/implements Job/*% } %*/ {

  @Inject private UMUserRepository userRepository;

  @Inject private UMUserService userService;

  @Value("${initDB.defaultAdminUser}")
  private String adminUser;

  @Value("${initDB.defaultAdminPassword}")
  private String adminPassword;

  @Value("${initDB.defaultAdminEmail}")
  private String adminEmail;

  private final Logger log = LoggerFactory.getLogger(InitDBJob.class);
  /*% if (feature.T_Quartz) { %*/
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
  /*% } else { %*/
  public void execute() {
  /*% } %*/
    try {
      if (userRepository.count() == 0) {
        log.debug("Creating user \"admin\" in the database");
        UMUser u;
        // Admin
        u =
            userService.createUser(
                adminUser, adminPassword, "Administrador", null, adminEmail, "en");
        Set<String> authorities = new HashSet<>(Arrays.asList(AuthoritiesConstants.ADMIN));
        userService.updateUser(u.getId(), u.getLogin(), u.getFirstName(), u.getLastName(), u.getEmail(),
          /*% if (feature.UM_AccountActivation) { %*/ true, /*% } %*/ u.getLangKey(), authorities/*% if (feature.MWM_EmployeeAuthentication) { %*/, null/*% } %*/);
        log.debug("User created successfully");
      }
    } catch (AccountException e) {
      log.error(e.getMessage());
    }
  }
}
/*% } %*/
