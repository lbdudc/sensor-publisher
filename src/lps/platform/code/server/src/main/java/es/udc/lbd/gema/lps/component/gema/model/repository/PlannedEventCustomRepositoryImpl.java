/*% if (feature.MWM_TE_VisitsRecord) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import javax.persistence.*;
import javax.persistence.EntityManager;
import java.util.Iterator;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import javax.persistence.Query;
import org.locationtech.jts.geom.Geometry;
import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

@Repository
public class PlannedEventCustomRepositoryImpl implements PlannedEventCustomRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Map<List<Long>, Geometry> findClusteredVisits(
      LocalDate startDate,
      LocalDate endDate,
      PlannedEventState state,
      List<Long> employees,
      List<Long> clients,
      Double distance) {
    // String query build
    String nativeQuery =
      "SELECT string_agg(cast(id as text), ',') as ids, st_astext(st_centroid(st_union(geom))) as point FROM ( " +
        "	WITH cluster_count as (select count(*) as count from ( " +
        "		SELECT unnest(ST_ClusterWithin(geom, :distance)) AS cluster  " +
        "    	FROM te_planned_event pe " +
        "		WHERE (coalesce(:startDate, null) is null or pe.date >= :startDate) " +
        "    	AND (coalesce(:endDate, null) is null or pe.date <= :endDate) ";

    // Necessary due native queries doesn't work with null value on lists
    if (employees != null) nativeQuery += " AND (pe.employee_id in :employees) ";
    if (clients != null) nativeQuery += " AND (pe.client_id in :clients) ";

    nativeQuery +=
      "		AND (coalesce(:state,null) is null or (pe.state = :state)) " +
        "	) as cluster_count) " +
        "	SELECT id, geom, ST_ClusterKMeans(geom, cast((select count from cluster_count) as integer)) over() AS cluster  " +
        "    	FROM te_planned_event pe " +
        "		WHERE (coalesce(:startDate, null) is null or pe.date >= :startDate) " +
        "    	AND (coalesce(:endDate, null) is null or pe.date <= :endDate) ";

    // Necessary due native queries doesn't work with null value on lists
    if (employees != null) nativeQuery += " AND (pe.employee_id in :employees) ";
    if (clients != null) nativeQuery += " AND (pe.client_id in :clients) ";

    nativeQuery +=
      "		AND (coalesce(:state,null) is null or (pe.state = :state)) " +
        ") as cluster_assigned_events " +
        "GROUP BY cluster";

    Query query = entityManager.createNativeQuery(nativeQuery);

    query.setParameter("startDate", startDate);
    query.setParameter("endDate", endDate);
    query.setParameter("distance", distance);
    query.setParameter("state", state.ordinal());
    if (employees != null) query.setParameter("employees", employees);
    if (clients != null) query.setParameter("clients", clients);

    List<Object[]> result = query.getResultList();

    WKTReader reader = new WKTReader();
    Map<List<Long>, Geometry> clusters = new HashMap<>();

    // conversion of string values to ID list and geometry
    for (Iterator<Object[]> iterator = result.iterator(); iterator.hasNext(); ) {
      Object[] tuple = (Object[]) iterator.next();
      Geometry point;
      String[] numbers = ((String) tuple[0]).split(",");
      List<Long> clusterIds = new ArrayList<Long>();
      for (int i = 0; i < numbers.length; i++) {
        clusterIds.add(Long.parseLong(numbers[i]));
      }
      try {
        point = reader.read((String) tuple[1]);
      } catch (ParseException e) {
        throw new RuntimeException();
      }
      clusters.put(clusterIds, point);
    }
    return clusters;
  }
}
/*% } %*/
