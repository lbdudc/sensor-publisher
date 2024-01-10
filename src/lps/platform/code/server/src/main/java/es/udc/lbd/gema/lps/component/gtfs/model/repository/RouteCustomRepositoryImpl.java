/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Route;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class RouteCustomRepositoryImpl implements RouteCustomRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public Map<Geometry, Route> findRoutesShapes(List<String> routeIds) {
    String sqlString =
        "SELECT ST_AsText(geom), tr.route_id, route_color, route_desc, "
            + "route_long_name, route_short_name, route_sort_order, route_text_color, route_type, route_url "
            + "FROM (VALUES ";
    if (routeIds.size() < 1) {
      return new HashMap<Geometry, Route>();
    }
    sqlString += "('" + routeIds.get(0) + "')";
    for (int i = 1; i < routeIds.size(); i++) {
      String id = routeIds.get(i);
      sqlString += ", ";
      sqlString += "('" + id + "')";
    }
    sqlString +=
        ") AS route_ids (route_id) "
            + "INNER JOIN t_routes tr ON route_ids.route_id = tr.route_id "
            + "INNER JOIN (select distinct route_id, shape_id from t_trips) tt ON tt.route_id = tr.route_id "
            + "INNER JOIN shapes_aggregated sa ON sa.shape_id = tt.shape_id";

    List<Object[]> result = entityManager.createNativeQuery(sqlString).getResultList();
    Map<Geometry, Route> geometryRoutes = new HashMap<Geometry, Route>();
    for (Object[] columns : result) {
      WKTReader reader = new WKTReader();
      Geometry geom;
      try {
        geom = reader.read((String) columns[0]);
      } catch (ParseException e) {
        geom = null;
      }
      Route route = new Route();

      route.setRouteId((String) columns[1]);
      route.setRouteColor((String) columns[2]);
      route.setRouteDesc((String) columns[3]);
      route.setRouteLongName((String) columns[4]);
      route.setRouteShortName((String) columns[5]);
      route.setRouteSortOrder((Integer) columns[6]);
      route.setRouteTextColor((String) columns[7]);
      route.setRouteType((Integer) columns[8]);
      route.setRouteUrl((String) columns[9]);

      geometryRoutes.put(geom, route);
    }
    return geometryRoutes;
  }
}
/*% } %*/
