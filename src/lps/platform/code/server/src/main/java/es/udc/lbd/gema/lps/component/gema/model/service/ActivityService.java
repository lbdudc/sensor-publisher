/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service;

import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
/*% if (feature.MWM_M_Client) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
/*% } %*/
/*% if (feature.MWM_M_Employee) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
/*% } %*/
/*% if (feature.MWM_M_PlannedVisit) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
/*% } %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTemporalRelation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTimeComparation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.DelimitedZoneSearchType;
import es.udc.lbd.gema.lps.component.gema.model.repository.ActivityCategoryRepository;
import es.udc.lbd.gema.lps.component.gema.model.repository.ActivityRepository;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityDistanceDurationDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityStatisticsDTO;
import es.udc.lbd.gema.lps.config.Properties;
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import es.udc.lbd.gema.lps.model.domain.user_management.Authority;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.service.UMUserService;
import java.util.ArrayList;
import es.udc.lbd.gema.lps.security.SecurityUtils;
/*% } %*/
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityService {

  @Inject private ActivityRepository activityRepository;

  @Inject private ActivityCategoryRepository activityCategoryRepository;
  /*% if (feature.MWM_EmployeeAuthentication) { %*/

  @Inject private UMUserService umUserService;
  /*% } %*/
  /*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/

  @Inject private Properties properties;

  public List<ActivityDTO> findAll(
    List<Long> categoriesDTO,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    List<Long> employeesDTO,
    List<Long> clientsDTO,
    List<Long> eventsDTO,
    Geometry bbox) {

    return this.findAll(
      categoriesDTO,
      startDate,
      endDate,
      startTime,
      endTime,
      employeesDTO,
      clientsDTO,
      eventsDTO,
      bbox,
      null,
      null,
      null,
      null,
      null);
  }

  public List<ActivityDTO> findAll(
      List<Long> categoriesDTO,
      LocalDate startDate,
      LocalDate endDate,
      List<Long> employeesDTO,
      List<Long> clientsDTO,
      List<Long> eventsDTO,
      Geometry bbox,
      DelimitedZoneSearchType delimitedZoneSearchType,
      AdvancedQueryTemporalRelation advancedQueryTemporalRelation,
      Long advancedQueryCategoryId,
      AdvancedQueryTimeComparation advancedQueryTimeComparation,
      Long advancedQueryTime) {
    return this.findAll(
        categoriesDTO,
        startDate,
        endDate,
        null,
        null,
        employeesDTO,
        clientsDTO,
        eventsDTO,
        bbox,
        delimitedZoneSearchType,
        advancedQueryTemporalRelation,
        advancedQueryCategoryId,
        advancedQueryTimeComparation,
        advancedQueryTime);
  }

  /**
   * Get all the activities that meet the specified filters
   *
   * @param categoriesDTO: DTOs of the categories by which you want to filter
   * @param startDate: initial date of the time range for which you want to filter
   * @param endDate: final date of the time range for which you want to filter
   * @param startTime: initial time of the startDate for which you want to filter. Default is 00:00
   * @param endTime: final time of the endDate for which you want to filter. Default is 23:59
   * @param employeesDTO: DTOs of the employees by which you want to filter
   * @param clientsDTO: DTOs of the clients by which you want to filter
   * @param eventsDTO: DTOs of the events by which you want to filter
   * @param bbox: geometry of the map area to be searched
   * @param delimitedZoneSearchType: search type ID of the area delimited by bbox { INSIDE | OUTSIDE | PARTIALLY_IN }
   * @param advancedQueryTemporalRelation: ID of the temporary relationship with
   *     advancedQueryCategory { BEFORE | AFTER }
   * @param advancedQueryCategory: Category ID of the previous/next activity
   * @param advancedQueryTimeComparation: activity duration comparation ID { GREATER | LESS }
   * @param advancedQueryTime: time by which the duration of the activities is compared (minutes)
   * @return list of ActivityDTOs
   */
  public List<ActivityDTO> findAll(
    List<Long> categoriesDTO,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    List<Long> employeesDTO,
    List<Long> clientsDTO,
    List<Long> eventsDTO,
    Geometry bbox,
    DelimitedZoneSearchType delimitedZoneSearchType,
    AdvancedQueryTemporalRelation advancedQueryTemporalRelation,
    Long advancedQueryCategoryId,
    AdvancedQueryTimeComparation advancedQueryTimeComparation,
    Long advancedQueryTime) {

    LocalDateTime startDateTime = null;
    LocalDateTime endDateTime = null;
    List<Client> clients = null;
    List<Employee> employees = null;
    List<PlannedEvent> events = null;
    List<ActivityCategory> categories = null;
    ActivityCategory advancedQueryCategory = null;

    if (startDate != null) {
      startDateTime = (startTime != null) ? startDate.atTime(startTime) : startDate.atStartOfDay();
    }
    if (endDate != null) {
      endDateTime = (endTime != null) ? endDate.atTime(endTime) : endDate.atTime(23, 59, 59);
    }

    if (employeesDTO != null) {
      employees =
        employeesDTO.stream()
          .map(
            id -> {
              Employee entity = new Employee();
              entity.setId(id);
              return entity;
            })
          .collect(Collectors.toList());
    }
    if (clientsDTO != null) {
      clients =
        clientsDTO.stream()
          .map(
            id -> {
              Client entity = new Client();
              entity.setId(id);
              return entity;
            })
          .collect(Collectors.toList());
    }
    if (eventsDTO != null) {
      events =
        eventsDTO.stream()
          .map(
            id -> {
              PlannedEvent entity = new PlannedEvent();
              entity.setId(id);
              return entity;
            })
          .collect(Collectors.toList());
    }
    if (categoriesDTO != null) {
      categories =
        categoriesDTO.stream()
          .map(
            id -> {
              ActivityCategory category = new ActivityCategory();
              category.setId(id);
              return category;
            })
          .collect(Collectors.toList());
    }

    if (advancedQueryTime != null) {
      // Convert minutes in milliseconds
      advancedQueryTime = advancedQueryTime * 60 * 1000;
    }

    if (bbox != null && delimitedZoneSearchType == null) {
      // If we received a delimited zone and not a type of search, we choose INSIDE by default
      delimitedZoneSearchType = delimitedZoneSearchType.INSIDE;
    }

    List<Activity> activities =
      activityRepository.findAll(
        categoriesDTO,
        startDateTime,
        endDateTime,
        employeesDTO,
        eventsDTO,
        bbox,
        delimitedZoneSearchType,
        properties.getGis().getDefaultSrid(),
        advancedQueryTemporalRelation,
        advancedQueryCategoryId,
        advancedQueryTimeComparation,
        advancedQueryTime);
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities(); //Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      activities =
        activities.stream()
          .filter(activity -> activity.getEmployee().getId() == umUser.getEmployee().getId())
          .collect(Collectors.toList());
    }
    /*% } %*/
    List<ActivityDTO> activitiesDTO =
      activities.stream().map(activity -> new ActivityDTO(activity)).collect(Collectors.toList());
    activitiesDTO.forEach(
      dto -> {
        ActivityDistanceDurationDTO activityDistanceDurationDTO =
          activityRepository.getActivityDistanceAndDuration(dto.getId());
        dto.setDistance(activityDistanceDurationDTO.getDistance());
        dto.setDuration(activityDistanceDurationDTO.getDuration());
      });
    return activitiesDTO;
  }
  /*% } %*/
  /*% if (feature.MWM_TE_Statistics || feature.MWM_TE_ActivitiesRecord) { %*/
  public List<ActivityStatisticsDTO> getActivitiesStatistics(
    LocalDate startDate, LocalDate endDate, List<Long> employeesIds, String type) {

    List<ActivityCategory> activityCategories = activityCategoryRepository.findAll();

    if (startDate == null) {
      startDate = getFirstDayWithActivities();
    }
    if (endDate == null) {
      endDate = getLastDayWithActivities();
    }
    /*% if (feature.MWM_EmployeeAuthentication) { %*/

    UMUser umUser = umUserService.getUserWithAuthorities(); //Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      employeesIds = new ArrayList<Long>();
      employeesIds.add(umUser.getEmployee().getId());
    }

    /*% } %*/
    List<ActivityStatisticsDTO> activityStatisticsDTOs =
      activityRepository.getActivitiesStatistics(
        startDate.atStartOfDay(),
        endDate.atTime(23, 59, 59),
        employeesIds,
        type,
        activityCategories);
    return activityStatisticsDTOs;
  }
  public LocalDate getFirstDayWithActivities() {
    LocalDateTime firstDay = activityRepository.findFirstDayWithActivities();

    return (firstDay == null) ? null : firstDay.toLocalDate();
  }

  public LocalDate getLastDayWithActivities() {
    LocalDateTime lastDay = activityRepository.findLastDayWithActivities();

    return (lastDay == null) ? null : lastDay.toLocalDate();
  }

  public List<Long> getActivitiesByPoint(Float pointLat, Float pointLng, Float precision) {
    return activityRepository.findActivitiesByPoint(pointLat, pointLng, precision);
  }
  /*% } %*/
}
/*% } %*/
