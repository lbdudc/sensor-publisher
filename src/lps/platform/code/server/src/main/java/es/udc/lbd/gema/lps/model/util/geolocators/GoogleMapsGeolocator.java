/*% if (feature.DM_A_G_GoogleMaps) { %*/
package es.udc.lbd.gema.lps.model.util.geolocators;

import es.udc.lbd.gema.lps.config.Constants;
import es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsJSON.GoogleMapsJSON;
import es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsJSON.GoogleMapsLocationJSON;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;


public class GoogleMapsGeolocator {
    private static final Logger logger = LoggerFactory.getLogger(GoogleMapsGeolocator.class);
    private static final int SRID = /*%= data.basicData.SRID || 4326  %*/;

    public static Point geolocate(String searchString) {
        return GoogleMapsGeolocator.geolocate(Arrays.asList(searchString));
    }

    /**
     * Geolocates using Google Maps service
     *
     * @param searchString Location names, ordered from small to bigger location. <br>
     *                     Fields splitted by comma
     * @return Location coordinates or null if no coordinates found
     */
    public static Point geolocate(List<String> searchString) {
        try {
            // Clone searchString to avoid original list modifications
            List<String> fields = new ArrayList(searchString);
            // Create client with http authenticated proxy configuration
            Client client = Client.create();

            // Set GIS properties
            PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING);
            GeometryFactory gf = new GeometryFactory(pm, SRID);
            CoordinateSequenceFactory f = gf.getCoordinateSequenceFactory();
            CoordinateSequence cs = f.create(1, 2); // One coordinate with 2 dimensions

            while (!fields.isEmpty()) {
                URIBuilder u = new URIBuilder("https://maps.googleapis.com/maps/api/geocode/json");
                u.addParameter("address", StringUtils.join(fields, ","));
                u.addParameter("key", Constants.GOOGLE_MAPS_KEY);
                URI uri = u.build().toURL().toURI();
                logger.debug("URI: {}", uri);

                ClientResponse response = client.resource(uri).accept("application/json").get(ClientResponse.class);

                if (response.getStatus() != 200) {
                    logger.error("Error retrieving geolocation from Google Maps. HTTP Error code: " + response.getStatus());
                }

                GoogleMapsJSON googleJson = new ObjectMapper().readValue(response.getEntity(String.class), GoogleMapsJSON.class);

                if (googleJson.getStatus().equals("OK")) {
                    // If NOT empty results: Use the FIRST result
                    GoogleMapsLocationJSON location = googleJson.getResults().get(0).getGeometry().getLocation();

                    // PostGIS: x-> Longitude, y-> Latitude
                    cs.setOrdinate(0, 0, location.getLng());
                    cs.setOrdinate(0, 1, location.getLat());

                    logger.debug("Lat: {}, Lon: {} ", location.getLat(), location.getLng());
                    return new Point(cs, gf);
                } else if (googleJson.getStatus().equals("ZERO_RESULTS")) {
                    logger.debug("No results");
                    searchString.remove(searchString.size() - 1);
                } else {
                    logger.error("Error querying google maps: {}. {} ", googleJson.getStatus(), googleJson.getError_message());
                }
            }
        } catch (Exception e) {
            logger.error("Error geolocating entity via Google Maps", e);
        }
        return null;
    }
}
/*% } %*/
