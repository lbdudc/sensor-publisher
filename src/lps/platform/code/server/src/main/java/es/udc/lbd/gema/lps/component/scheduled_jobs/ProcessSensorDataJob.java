/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.scheduled_jobs;

import java.io.IOException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.SensorDataProcessorService;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.exception.ConversionException;

  /*% if (feature.T_Quartz) { %*/
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
  /*% } else { %*/
import org.springframework.stereotype.Component;
  /*% } %*/

/*% if (!feature.T_Quartz) { %*/@Component/*% } %*/
public class ProcessSensorDataJob /*% if (feature.T_Quartz) { %*/implements Job /*% } %*/{

  @Inject private SensorDataProcessorService sensorDataProcessorService;

  private final Logger log = LoggerFactory.getLogger(ProcessSensorDataJob.class);

  /*% if (feature.T_Quartz) { %*/
  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
  /*% } else { %*/
  public void execute() {
  /*% } %*/
    try {
		  sensorDataProcessorService.processSensorData(false);
	  } catch (IOException | ConversionException e) {
		  log.error(e.getMessage());
	  }
  }
}
/*% } %*/
