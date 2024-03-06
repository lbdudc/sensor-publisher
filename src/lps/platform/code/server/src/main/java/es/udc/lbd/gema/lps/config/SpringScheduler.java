package es.udc.lbd.gema.lps.config;

import org.springframework.context.annotation.Configuration;
/*% if (feature.MWM_TA_TrajectoryAnnotator || feature.T_FileUploader || feature.UM_AccountActivation || feature.UserManagement) { %*/
import es.udc.lbd.gema.lps.component.scheduled_jobs.*;
/*% } %*/
/*% if (feature.T_Quartz) { %*/
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.scheduling.quartz.*;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
/*% } else { %*/
import javax.inject.Inject;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import javax.annotation.PostConstruct;
/*% } %*/

@Configuration
/*% if (feature.T_Quartz) { %*/
@EnableAutoConfiguration
/*% } else { %*/
@EnableScheduling
/*% } %*/
public class SpringScheduler {

  /*% if (feature.T_Quartz) { %*/
  @Autowired private QuartzProperties quartzProperties;
  @Autowired private ApplicationContext applicationContext;
      /*% if (feature.T_FileUploader) { %*/
  private final String DAILY_AT_SIX = "0 0 6 ? * * *";
      /*% } %*/
      /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
  private final String DAILY_AT_THREE = "0 0 3 ? * * *";
      /*% } %*/
      /*% if (feature.UM_AccountActivation) { %*/
  private final String DAILY_AT_ONE = "0 0 1 ? * * *";
      /*% } %*/
      /*% if (feature.UserManagement) { %*/
  private final String DAILY_AT_TWELVE = "0 0 0 ? * * *";
      /*% } %*/

  @Bean
  public SpringBeanJobFactory springBeanJobFactory() {
    AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();

    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  @Bean
  public SchedulerFactoryBean scheduler(DataSource quartzDataSource) {

    SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();

    schedulerFactory.setJobFactory(springBeanJobFactory());
    schedulerFactory.setSchedulerName("qrtz_scheduler");
    schedulerFactory.setQuartzProperties(asProperties(quartzProperties.getProperties()));

    // All jobs to schedule
    Trigger[] triggers = {
        /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
      processSensorDataTrigger().getObject(),
        /*% } %*/
        /*% if (feature.UserManagement) { %*/
      removeOldPersistentTokensTrigger().getObject(),
      initDBJobTrigger().getObject(),
        /*% } %*/
        /*% if (feature.T_FileUploader) { %*/
      removeOldTemporaryFilesTrigger().getObject(),
        /*% } %*/
        /*% if (feature.UM_AccountActivation) { %*/
      removeNotActivatedUsersTrigger().getObject(),
        /*% } %*/
        /*% if (feature.MV_MS_GJ_Cached) { %*/
      generateSpatialFilesTrigger().getObject(),
        /*% } %*/
    };
    schedulerFactory.setTriggers(triggers);
    schedulerFactory.setDataSource(quartzDataSource);

    return schedulerFactory;
  }

  /*
   * Jobs & triggers
   */
      /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
  @Bean
  public JobDetailFactoryBean processSensorDataJobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(ProcessSensorDataJob.class);
    jobDetailFactory.setDurability(true);
    jobDetailFactory.setGroup("sensor-data");
    return jobDetailFactory;
  }

  @Bean
  public CronTriggerFactoryBean processSensorDataTrigger() {
    CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
    trigger.setJobDetail(processSensorDataJobDetail().getObject());
    trigger.setGroup("sensor-data");
    trigger.setCronExpression(DAILY_AT_THREE);
    return trigger;
  }
      /*% } %*/
      /*% if (feature.UserManagement) { %*/
  @Bean
  public JobDetailFactoryBean removeOldPersistentTokensJobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(RemoveOldPersistentTokensJob.class);
    jobDetailFactory.setDurability(true);
    jobDetailFactory.setGroup("user-management");
    return jobDetailFactory;
  }

  @Bean
  public CronTriggerFactoryBean removeOldPersistentTokensTrigger() {
    CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
    trigger.setJobDetail(removeOldPersistentTokensJobDetail().getObject());
    trigger.setGroup("user-management");
    trigger.setCronExpression(DAILY_AT_TWELVE);
    return trigger;
  }

  @Bean
  public JobDetailFactoryBean initDBJobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(InitDBJob.class);
    jobDetailFactory.setDurability(true);
    jobDetailFactory.setGroup("user-management");
    return jobDetailFactory;
  }

  @Bean
  public SimpleTriggerFactoryBean initDBJobTrigger() {
    SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
    trigger.setJobDetail(initDBJobDetail().getObject());
    trigger.setGroup("user-management");
    trigger.setRepeatCount(0);
    return trigger;
  }
      /*% } %*/
      /*% if (feature.T_FileUploader) { %*/
  @Bean
  public JobDetailFactoryBean removeOldTemporaryFilesJobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(RemoveOldTemporaryFilesJob.class);
    jobDetailFactory.setDurability(true);
    jobDetailFactory.setGroup("remove-files");
    return jobDetailFactory;
  }

  @Bean
  public CronTriggerFactoryBean removeOldTemporaryFilesTrigger() {
    CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
    trigger.setJobDetail(removeOldTemporaryFilesJobDetail().getObject());
    trigger.setGroup("remove-files");
    trigger.setCronExpression(DAILY_AT_SIX);
    return trigger;
  }
      /*% } %*/
      /*% if (feature.UM_AccountActivation) { %*/
  @Bean
  public JobDetailFactoryBean removeNotActivatedUsersJobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(RemoveNotActivatedUsersJob.class);
    jobDetailFactory.setDurability(true);
    jobDetailFactory.setGroup("user-management");
    return jobDetailFactory;
  }

  @Bean
  public CronTriggerFactoryBean removeNotActivatedUsersTrigger() {
    CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
    trigger.setJobDetail(removeNotActivatedUsersJobDetail().getObject());
    trigger.setGroup("user-management");
    trigger.setCronExpression(DAILY_AT_ONE);
    return trigger;
  }
        /*% } %*/
        /*% if (feature.MV_MS_GJ_Cached) { %*/
  @Bean
  public JobDetailFactoryBean generateSpatialFilesJobDetail() {
    JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
    jobDetailFactory.setJobClass(GenerateSpatialFilesJob.class);
    jobDetailFactory.setDurability(true);
    jobDetailFactory.setGroup("initial-data");
    return jobDetailFactory;
  }
  @Bean
  public SimpleTriggerFactoryBean generateSpatialFilesTrigger() {
    SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
    trigger.setJobDetail(generateSpatialFilesJobDetail().getObject());
    trigger.setGroup("initial-data");
    trigger.setRepeatCount(0);
    return trigger;
  }
        /*% } %*/

  /*
   * Util methods
   */

  private java.util.Properties asProperties(Map source) {
    java.util.Properties properties = new java.util.Properties();
    properties.putAll(source);
    return properties;
  }
  /*% } else { %*/
      /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
  private final String DAILY_AT_THREE = "0 0 3 * * *";
  @Inject private ProcessSensorDataJob processSensorDataJob;

      /*% } %*/
      /*% if (feature.UserManagement) { %*/
  private final String DAILY_AT_TWELVE = "0 0 0 * * *";
  @Inject private RemoveOldPersistentTokensJob removeOldPersistentTokensJob;
  @Inject private InitDBJob initDBJob;

      /*% } %*/
      /*% if (feature.T_FileUploader) { %*/
  private final String DAILY_AT_SIX = "0 0 6 * * *";
  @Inject private RemoveOldTemporaryFilesJob removeOldTemporaryFilesJob;

      /*% } %*/
      /*% if (feature.UM_AccountActivation) { %*/
  private final String DAILY_AT_ONE = "0 0 1 * * *";
  @Inject private RemoveNotActivatedUsersJob removeNotActivatedUsersJob;

      /*% } %*/
      /*% if (feature.MV_MS_GJ_Cached) { %*/
  @Inject private GenerateSpatialFilesJob generateSpatialFilesJob;

      /*% } %*/
    /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
  @Scheduled(cron = DAILY_AT_THREE)
  public void processSensorData() {
    processSensorDataJob.execute();
  }
      /*% } %*/

      /*% if (feature.UserManagement) { %*/
  @Scheduled(cron = DAILY_AT_TWELVE)
  public void removeOldPersistentTokens() {
    removeOldPersistentTokensJob.execute();
  }

  @PostConstruct
  public void initDB() {
    initDBJob.execute();
  }
      /*% } %*/

      /*% if (feature.T_FileUploader) { %*/
  @Scheduled(cron = DAILY_AT_SIX)
  public void removeOldTemporaryFiles() {
    removeOldTemporaryFilesJob.execute();
  }
      /*% } %*/

      /*% if (feature.UM_AccountActivation) { %*/
  @Scheduled(cron = DAILY_AT_ONE)
  public void removeNotActivatedUsers() {
    removeNotActivatedUsersJob.execute();
  }
      /*% } %*/

      /*% if (feature.MV_MS_GJ_Cached) { %*/
  @PostConstruct
  public void generateSpatialFiles() {
    generateSpatialFilesJob.execute();
  }
      /*% } %*/
  /*% } %*/
}
