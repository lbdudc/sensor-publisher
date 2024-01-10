/*% if (feature.MWM_M_PlannedVisit) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
/*% if (feature.MWM_VisitType) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.time.LocalDate;
/*% } %*/
/*% if (feature.MWM_VisitSchedule) { %*/
import java.util.Set;
import javax.persistence.Tuple;
/*% } %*/

public interface PlannedEventRepository
  extends JpaRepository<PlannedEvent, Long>,
  /*% if (feature.MWM_TE_VisitsRecord) { %*/PlannedEventCustomRepository,/*% } %*/
  JpaSpecificationExecutor<PlannedEvent> {

  /*% if (feature.MVM_VT_OnlyDay) { %*/
    public List<PlannedEvent> findByDateAndEmployeeOrderByDate(
      LocalDate date, Employee employee);

  public List<PlannedEvent> findByDateAndEmployeeAndEventOrderGreaterThan(
    LocalDate startDate, Employee employee, Integer eventOrder);
  /*% } %*/
  /*% if (feature.MWM_VisitSchedule) { %*/
  @Query(
      "select pe from PlannedEvent pe "
          + "join Employee e on pe.employee.id = e.id "
          + "where (coalesce(:employees, null) is null or e.id in :employees) "
          + "and (coalesce(:startDate, null) is null or pe.date > :startDate) "
          + "and (coalesce(:endDate, null) is null or pe.date < :endDate)")
  public Page<PlannedEvent> findByEmployeeIdInAndDateBetween(
      @Param(value = "employees") List<Long> employees,
      @Param(value = "startDate") LocalDate startDate,
      @Param(value = "endDate") LocalDate endDate,
      Pageable createPageable);

  @Query(
      "select pe.date as date, count(pe) as count from PlannedEvent pe "
          + "join Employee e on pe.employee.id = e.id "
          + "where (coalesce(:employees, null) is null or e.id in :employees) "
          + "and (coalesce(:startDate, null) is null or pe.date > :startDate) "
          + "and (coalesce(:endDate, null) is null or pe.date < :endDate) "
          + "group by pe.date "
          + "having count(pe) > :threshold")
  public List<Tuple> countByEmployeeIdInAndDateBetween(
      @Param(value = "employees") List<Long> employees,
      @Param(value = "startDate") LocalDate startDate,
      @Param(value = "endDate") LocalDate endDate,
      @Param(value = "threshold") Long threshold);

  @Query(
      "select pe from PlannedEvent pe "
          + "join Employee e on pe.employee.id = e.id "
          + "where (coalesce(:employees, null) is null or e.id in :employees) "
          + "and (coalesce(:startDate, null) is null or pe.date > :startDate) "
          + "and (coalesce(:endDate, null) is null or pe.date < :endDate) "
          + "and (coalesce(:dates, null) is null or pe.date NOT IN (:dates)) ")
  public List<PlannedEvent> findByEmployeeIdInAndDateNotIn(
      @Param(value = "employees") List<Long> employees,
      @Param(value = "dates") Set<LocalDate> dates,
      @Param(value = "startDate") LocalDate startDate,
      @Param(value = "endDate") LocalDate endDate);
  /*% } %*/
  /*% if (feature.MWM_TE_VisitsRecord) { %*/
  @Query(
      "select pe from PlannedEvent pe "
          + "where (coalesce(:ids, null) is null or pe.id in :ids)")
  public Page<PlannedEvent> findAllById(
      @Param(value = "ids") List<Long> eventIds,
      Pageable pageable);
  /*% } %*/
}
/*% } %*/
