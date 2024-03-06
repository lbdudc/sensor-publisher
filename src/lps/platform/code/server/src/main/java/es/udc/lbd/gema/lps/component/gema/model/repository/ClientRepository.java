/*% if (feature.MWM_M_Client) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
/*% if (feature.MWM_TE_VisitsRecord ) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientStatisticsDTO;
import java.time.LocalDate;
/*% } %*/
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ClientRepository
  extends JpaRepository<Client, Long>,
  JpaSpecificationExecutor<Client>,
  PagingAndSortingRepository<Client, Long> {

  @Query(
      "select c from Client c "
          + "where (coalesce(:fullName,null) is null or (c.fullName in :fullName)) "
          + "and (:bbox is null or within(c.location, :bbox) = true) "
          + "and c.active = true "
          + "order by c.fullName")
  List<Client> findByFullNameInAndLocationWithinAndActiveTrueOrderByFullName(
      @Param("fullName") List<String> fullName, @Param("bbox") Geometry bbox);

  Optional<Client> findById(Long id);
  /*% if (feature.MWM_TE_VisitsRecord) { %*/
  @Query(
      "select distinct c "
          + "from PlannedEvent pe inner join pe.client c "
          + "where (coalesce(:state,null) is null or pe.state = :state) "
          + "and ((coalesce(:end,null) is null and pe.date = :start) "
          + "or (coalesce(:end,null) is not null and pe.date <= :end and pe.date >= :start))")
  List<Client> findByHasVisitsBetween(LocalDate start, LocalDate end, PlannedEventState state);

  @Query(
      "select new es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientStatisticsDTO("
          + "c, count(pe.id)/*% if (feature.MVM_VT_WithTime) { %*/, sum(extract(epoch from ((pe.endTime - pe.startTime)*1000)))/*% } %*/) "
          + "from PlannedEvent pe inner join pe.client c inner join pe.employee e "
          + "where (coalesce(:clients,null) is null or (c.id in :clients)) "
          + "and (coalesce(:employees,null) is null or (e.id in :employees)) "
          + "and (coalesce(:start,null) is null or (pe.date >= :start)) "
          + "and (coalesce(:end,null) is null or (pe.date <= :end)) "
          + "and (coalesce(:state,null) is null or (pe.state = :state)) "
          + "group by c")
  List<ClientStatisticsDTO> getVisitsStatistics(
      List<Long> clients, List<Long> employees, LocalDate start, LocalDate end, PlannedEventState state);
  /*% } %*/
}
/*% } %*/
