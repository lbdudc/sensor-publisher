/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
/*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.time.LocalDateTime;
import java.util.List;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
/*% } %*/

public interface ActivityRepository extends JpaRepository<Activity, Long>, ActivityCustomRepository {
  /*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/
  @Query(value = "SELECT ts_init FROM te_activity ORDER BY ts_init ASC LIMIT 1", nativeQuery = true)
  LocalDateTime findFirstDayWithActivities();

  @Query(
    value = "SELECT ts_init FROM te_activity ORDER BY ts_init DESC LIMIT 1",
    nativeQuery = true)
  LocalDateTime findLastDayWithActivities();

  @Query(
    value =
      "SELECT id FROM te_activity WHERE ST_Intersects(ST_Buffer(ST_SetSRID(ST_Point(:lng, :lat), 4326), :precision), geom) ORDER BY id",
    nativeQuery = true)
  List<Long> findActivitiesByPoint(
    @Param(value = "lat") Float lat,
    @Param(value = "lng") Float lng,
    @Param(value = "precision") Float precision);
  /*% } %*/
}
/*% } %*/
