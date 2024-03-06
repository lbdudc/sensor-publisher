/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Trip;

public interface TripCustomRepository {

	Map<Geometry, Trip> findTripsShapes(List<String> collect);
}
/*% } %*/
