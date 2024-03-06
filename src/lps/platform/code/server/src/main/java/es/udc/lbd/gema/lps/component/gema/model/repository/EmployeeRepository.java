/*% if (feature.MWM_M_Employee) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/*% if (feature.MWM_TE_VisitsRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeStatisticsDTO;
import java.time.LocalDate;
/*% } %*/
/*% if (feature.MWM_TE_ActivitiesRecord) { %*/
import java.time.LocalDateTime;
/*% } %*/

public interface EmployeeRepository
  extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor<Employee>,
    PagingAndSortingRepository<Employee, Long> {
  @Query(
      "select e from Employee e "
          + "where (coalesce(:fullName,null) is null or (e.fullName in :fullName)) "
          + "and (:bbox is null or within(e.location, :bbox) = true) "
          + "and e.active = true "
          + "order by e.fullName")
  List<Employee> findByFullNameInAndLocationWithinAndActiveTrueOrderByFullName(
      @Param("fullName") List<String> fullName, @Param("bbox") Geometry bbox);

  Optional<Employee> findById(Long id);

  @Query(
    "select e from Employee e "
      + "where (coalesce(:fullName,null) is null or (lower(e.fullName) like :fullName)) "
      + "and (coalesce(:ids,null) is null or (e.id not in :ids)) "
      + "order by e.fullName")
  List<Employee> findByFullNameInAndIdNotInOrderByFullName(
    @Param("ids") List<Long> ids, @Param("fullName") String fullName);
  /*% if (feature.MWM_TE_ActivitiesRecord) { %*/
    @Query(
      "select distinct e "
          + "from Activity a inner join a.employee e "
          + "where a.ts_end <= :end and a.ts_init >= :start")
  List<Employee> findByHasActivitiesBetween(LocalDateTime start, LocalDateTime end);
  /*% } %*/
  /*% if (feature.MWM_TE_VisitsRecord) { %*/
  @Query(
      "select distinct e "
          + "from PlannedEvent pe inner join pe.employee e "
          + "where (coalesce(:state,null) is null or pe.state = :state) "
          + "and ((coalesce(:end,null) is null and pe.date = :start) "
          + "or (coalesce(:end,null) is not null and pe.date <= :end and pe.date >= :start))")
  List<Employee> findByHasVisitsBetween(LocalDate start, LocalDate end, PlannedEventState state);
  
  @Query(
      "select new es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeStatisticsDTO("
          + "e, count(pe.id)) "
          + "from PlannedEvent pe inner join pe.employee e inner join pe.client c "
          + "where (coalesce(:clients,null) is null or (c.id in :clients)) "
          + "and (coalesce(:employees,null) is null or (e.id in :employees))"
          + "and (coalesce(:start,null) is null or (pe.date >= :start)) "
          + "and (coalesce(:end,null) is null or (pe.date <= :end)) "
          + "and (coalesce(:state,null) is null or (pe.state = :state)) "
          + "group by e")
  List<EmployeeStatisticsDTO> getVisitsStatistics(
      List<Long> clients, List<Long> employees, LocalDate start, LocalDate end, PlannedEventState state);
  /*% } %*/
}
/*% } %*/
