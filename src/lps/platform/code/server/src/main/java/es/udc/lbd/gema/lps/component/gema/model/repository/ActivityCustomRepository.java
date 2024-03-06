/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

/*% if (feature.MWM_TE_Statistics || feature.MWM_TE_ActivitiesRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityStatisticsDTO;
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
/*% } %*/
/*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTemporalRelation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTimeComparation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.DelimitedZoneSearchType;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityDistanceDurationDTO;
import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import org.locationtech.jts.geom.Geometry;
/*% } %*/
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityCustomRepository {
  /*% if (feature.MWM_TE_Statistics || feature.MWM_TE_ActivitiesRecord) { %*/
  public List<ActivityStatisticsDTO> getActivitiesStatistics(
    LocalDateTime start,
    LocalDateTime end,
    List<Long> employees,
    String type,
    List<ActivityCategory> categories);
  /*% } %*/
  /*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/
  public ActivityDistanceDurationDTO getActivityDistanceAndDuration(Long activity);

  public List<Activity> findAll(
    List<Long> categories,
    LocalDateTime start,
    LocalDateTime end,
    List<Long> employees,
    List<Long> events,
    Geometry bbox,
    DelimitedZoneSearchType delimitedZoneSearchType,
    Integer SRID,
    AdvancedQueryTemporalRelation advancedQueryTemporalRelation,
    Long advancedQueryCategoryId,
    AdvancedQueryTimeComparation advancedQueryTimeComparation,
    Long advancedQueryTime);
  /*% } %*/
}
/*% } %*/
