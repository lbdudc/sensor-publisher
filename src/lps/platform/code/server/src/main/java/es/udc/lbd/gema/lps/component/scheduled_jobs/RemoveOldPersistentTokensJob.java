/*% if (feature.UserManagement) { %*/
package es.udc.lbd.gema.lps.component.scheduled_jobs;

import javax.inject.Inject;
import es.udc.lbd.gema.lps.model.service.UMUserService;
  /*% if (feature.T_Quartz) { %*/
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
  /*% } else { %*/
import org.springframework.stereotype.Component;
  /*% } %*/

/*% if (!feature.T_Quartz) { %*/@Component/*% } %*/
public class RemoveOldPersistentTokensJob /*% if (feature.T_Quartz) { %*/implements Job/*% } %*/ {

  @Inject private UMUserService umUserService;

  /*% if (feature.T_Quartz) { %*/
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
  /*% } else { %*/
  public void execute() {
  /*% } %*/
    umUserService.removeOldPersistentTokens();
  }
}
/*% } %*/
