/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Route;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Geometry;

public interface RouteCustomRepository {
  Map<Geometry, Route> findRoutesShapes(List<String> routeIds);
}
/*% } %*/
