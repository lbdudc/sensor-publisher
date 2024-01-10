/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import javax.persistence.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
/*% if (feature.MWM_TE_Statistics || feature.MWM_TE_ActivitiesRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityStatisticsDTO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

/*% } %*/
/*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTemporalRelation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTimeComparation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.DelimitedZoneSearchType;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityDistanceDurationDTO;
import org.hibernate.transform.Transformers;
import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import org.locationtech.jts.geom.Geometry;
/*% } %*/

@Repository
public class ActivityCustomRepositoryImpl implements ActivityCustomRepository {

  @PersistenceContext private EntityManager entityManager;

  /*% if (feature.MWM_TE_ActivitiesRecord || feature.MWM_TE_Planning || feature.MWM_TE_Statistics || feature.MWM_TA_SensorDataCollector) { %*/
  private String idsListToString(List<Long> employees) {
    String ret = "NULL";
    if (employees != null && employees.size() > 0) {
      ret = StringUtils.join(employees, ",");
    }
    return ret;
  }

  private List<String> getAliases(String queryString) {
    ArrayList<String> list = new ArrayList<String>(Arrays.asList(queryString.split("as \"")));
    List<String> result = new ArrayList<String>();
    list.remove(0);
    for (String str : list) {
      result.add(str.substring(0, str.indexOf("\"")));
    }
    return result;
  }

  @Override
  public List<ActivityStatisticsDTO> getActivitiesStatistics(
    LocalDateTime start,
    LocalDateTime end,
    List<Long> employees,
    String type,
    List<ActivityCategory> categories) {

    String employeesString = idsListToString(employees);
    long days = 1L;

    if (type != null && start != null && end != null && type.equals("Average")) {
      days = start.until(end, ChronoUnit.DAYS) + 1;
    }

    String queryString =
      "SELECT a.employee_id as \"employee_id\", "
        + "(SELECT b.full_name as \"employee_name\" FROM te_employee b WHERE (b.id = a.employee_id)) ";

    for (ActivityCategory category : categories) {

      // Time by category
      queryString +=
        ", (SELECT CAST(COALESCE(SUM(extract(epoch from ((b.ts_end - b.ts_init)*1000)))/"
          + days
          + ",0) AS DECIMAL) as \""
          + category.getLabel()
          + "\" FROM te_activity b WHERE ('"
          + start
          + "' IS null OR b.ts_init >= '"
          + start
          + "') AND ('"
          + end
          + "' IS null OR b.ts_end <= '"
          + end
          + "') AND b.employee_id = a.employee_id AND b.category_id = "
          + category.getId()
          + ") ";

      // Distance by category
      queryString +=
        ", (SELECT COALESCE(SUM(ST_Length(b.geom, true))/"
          + days
          + ",0) as \""
          + category.getLabel()
          + "_distance\" FROM te_activity b WHERE ('"
          + start
          + "' IS null OR b.ts_init >= '"
          + start
          + "') AND ('"
          + end
          + "' IS null OR b.ts_end <= '"
          + end
          + "') AND b.employee_id = a.employee_id AND b.category_id = "
          + category.getId()
          + ") ";
    }

    // Total duration
    queryString +=
      ", (SELECT CAST(COALESCE(SUM(extract(epoch from ((b.ts_end - b.ts_init)*1000)))/"
        + days
        + ",0) AS DECIMAL) as \"total\""
        + " FROM te_activity b WHERE ('"
        + start
        + "' IS null OR b.ts_init >= '"
        + start
        + "') AND ('"
        + end
        + "' IS null OR b.ts_end <= '"
        + end
        + "') AND b.employee_id = a.employee_id"
        + ") ";

    // Total distance
    queryString +=
      ", (SELECT COALESCE(SUM(ST_Length(b.geom, true))/"
        + days
        + ",0) as \"total_distance\""
        + " FROM te_activity b WHERE ('"
        + start
        + "' IS null OR b.ts_init >= '"
        + start
        + "') AND ('"
        + end
        + "' IS null OR b.ts_end <= '"
        + end
        + "') AND b.employee_id = a.employee_id "
        + ") ";

    queryString +=
      "FROM te_activity a "
        + "WHERE (("
        + employeesString
        + ") IS NULL OR a.employee_id IN ("
        + employeesString
        + ")) "
        + "GROUP BY a.employee_id";

    List<String> parameterNames = getAliases(queryString);

    Session session = entityManager.unwrap(Session.class);
    List<Object[]> results = session.createSQLQuery(queryString).list();

    List<ActivityStatisticsDTO> activityStatisticsDTOList = new ArrayList<ActivityStatisticsDTO>();
    ActivityStatisticsDTO activityStatisticsDTO;
    Map<String, String> resultsMap;

    for (Object[] item : results) {
      activityStatisticsDTO = new ActivityStatisticsDTO();
      resultsMap = new HashMap<String, String>();
      for (int i = 0; i < item.length; i++) {
        resultsMap.put(parameterNames.get(i), item[i].toString());
      }
      activityStatisticsDTO.setValues(resultsMap);
      activityStatisticsDTOList.add(activityStatisticsDTO);
    }

    return activityStatisticsDTOList;
  }
  /*% } %*/
  /*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TA_SensorDataCollector) { %*/
  @Override
  public ActivityDistanceDurationDTO getActivityDistanceAndDuration(Long activity) {

    String queryString =
      "SELECT "
        + "COALESCE(SUM(ST_Length(a.geom, true)),0) as distance, "
        + "CAST(COALESCE(SUM(extract(epoch from ((a.ts_end - a.ts_init)*1000))),0) AS DECIMAL) as duration "
        + "FROM te_activity a "
        + "WHERE a.id = :activity";

    Query query = entityManager.createNativeQuery(queryString);
    query.setParameter("activity", activity);

    query
      .unwrap(org.hibernate.query.Query.class)
      .setResultTransformer(Transformers.aliasToBean(ActivityDistanceDurationDTO.class));

    return (ActivityDistanceDurationDTO) query.getSingleResult();
  }

  @Override
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
    Long advancedQueryTime) {

    String employeesString = idsListToString(employees);
    String categoriesString = idsListToString(categories);
    String eventsString = idsListToString(events);
    String startString = start != null ? "'" + start + "'" : null;
    String endString = end != null ? "'" + end + "'" : null;

    String queryString =
      "SELECT a.id, a.ts_init, a.ts_end, a.event_id, a.employee_id, a.category_id, a.geom FROM te_activity a WHERE ("
        + startString
        + " IS null OR a.ts_init >= "
        + startString
        + ") "
        + "AND ("
        + endString
        + " IS null OR a.ts_end <= "
        + endString
        + ") ";
    if (categoriesString != "NULL") {
      queryString += "AND (" + "a.category_id IN (" + categoriesString + ")) ";
    }
    if (employeesString != "NULL") {
      queryString += "AND (a.employee_id IN (" + employeesString + ")) ";
    }
    if (eventsString != "NULL") {
      queryString += "AND (a.event_id IN (" + eventsString + ")) ";
    }
    if (bbox != null && delimitedZoneSearchType != null) {
      String delimitFunction = "";
      switch (delimitedZoneSearchType) {
        case INSIDE:
          delimitFunction = "ST_Within";
          break;
        case OUTSIDE:
          delimitFunction = "ST_Disjoint ";
          break;
        case PARTIALLY_IN:
          delimitFunction = "ST_Intersects";
          break;
      }
      queryString +=
        "AND ("
          + delimitFunction
          + "(a.geom, ST_GeomFromText('"
          + bbox
          + "',"
          + SRID
          + ")) = TRUE) ";
    }
    if (advancedQueryTimeComparation != null) {
      String comparationSymbol =
        (advancedQueryTimeComparation == AdvancedQueryTimeComparation.GREATER) ? ">" : "<";
      queryString +=
        "AND (cast(extract(epoch from (a.ts_end - a.ts_init))*1000 as bigint) "
          + comparationSymbol
          + advancedQueryTime
          + ") ";
    }
    if (advancedQueryCategoryId != null) {
      String symbol =
        (advancedQueryTemporalRelation == AdvancedQueryTemporalRelation.AFTER) ? ">" : "<";
      String order =
        (advancedQueryTemporalRelation == AdvancedQueryTemporalRelation.AFTER) ? "ASC" : "DESC";
      queryString +=
        "AND (("
          + advancedQueryCategoryId
          + ") = (SELECT b.category_id FROM te_activity b WHERE ((a.employee_id = b.employee_id) AND (b.ts_init "
          + symbol
          + " a.ts_init)) ORDER BY b.ts_init "
          + order
          + " LIMIT 1))";
    }

    List<Activity> result =
      entityManager
        .unwrap(Session.class)
        .createSQLQuery(queryString)
        .addEntity("a", Activity.class)
        .list();

    return result;
  }
  /*% } %*/
}
/*% } %*/
