/*% if (feature.MWM_TrajectoryAnnotation) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationRepository extends JpaRepository<Location, Long> {

  List<Location> findByTimeAfterAndTimeBeforeAndEmployeeOrderByTime(
      LocalDateTime startTime, LocalDateTime endTime, Employee employee);

  @Query(
      "select l "
          + "from Location l "
          + "where l.employee = ?1 "
          + "and (l.employee.lastProcessed is null "
          + "or l.employee.lastProcessed < l.creationDate) "
          + "order by l.time")
  List<Location> findByEmployeeAndCreationDateAfterOrderByTime(Employee employee);
}
/*% } %*/
