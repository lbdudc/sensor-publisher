/*% if (feature.MWM_TrajectoryAnnotation) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Segment;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SegmentRepository extends JpaRepository<Segment, Long> {

  @Query(
      "select s "
          + "from Segment s "
          + "where s.employee = ?1 "
          + "and (s.employee.lastProcessed is null "
          + "or s.employee.lastProcessed < s.creationDate) "
          + "order by s.startTime")
  List<Segment> findByEmployeeAndCreationDateAfterOrderByStartTime(Employee employee);

  List<Segment> findByStartTimeAfterAndEndTimeBeforeAndEmployeeOrderByEndTime(
      LocalDateTime startTime, LocalDateTime endTime, Employee employee);
}

/*% } %*/
