/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Trip;

public class TripCustomRepositoryImpl implements TripCustomRepository {

	@PersistenceContext private EntityManager entityManager;

	@Override
	public Map<Geometry, Trip> findTripsShapes(List<String> tripIds) {
		String sqlString =
		        "SELECT ST_AsText(geom), tt.trip_id, bikes_allowed, block_id, direction_id, shape_id, trip_headsign, trip_short_name, wheelchair_accessible "
		            + "FROM (VALUES ";
		    if (tripIds.size() < 1) {
		      return new HashMap<Geometry, Trip>();
		    }
		    sqlString += "('" + tripIds.get(0) + "')";
		    for (int i = 1; i < tripIds.size(); i++) {
		      String id = tripIds.get(i);
		      sqlString += ", ";
		      sqlString += "('" + id + "')";
		    }
		    sqlString +=
		        ") AS trip_ids (trip_id) "
		            + "INNER JOIN t_trips tt ON tt.trip_id = trip_ids.trip_id "
		            + "INNER JOIN shapes_aggregated sa ON tt.shape_id = sa.shape_id";

		    List<Object[]> result = entityManager.createNativeQuery(sqlString).getResultList();
		    Map<Geometry, Trip> geometryTrips = new HashMap<Geometry, Trip>();
		    for (Object[] columns : result) {
		      WKTReader reader = new WKTReader();
		      Geometry geom;
		      try {
		        geom = reader.read((String) columns[0]);
		      } catch (ParseException e) {
		        geom = null;
		      }
		      Trip trip = new Trip();

		      trip.setTripId((String) columns[1]);
		      trip.setBikesAllowed((Integer) columns[2]);
		      trip.setBlockId((String) columns[3]);
		      trip.setDirectionId((Integer) columns[4]);
		      trip.setShapeId((String) columns[5]);
		      trip.setTripHeadsign((String) columns[6]);
		      trip.setTripShortName((String) columns[7]);
		      trip.setWheelchairAccessible((Integer) columns[8]);

		      geometryTrips.put(geom, trip);
		    }
		    return geometryTrips;
	}

}
/*% } %*/
