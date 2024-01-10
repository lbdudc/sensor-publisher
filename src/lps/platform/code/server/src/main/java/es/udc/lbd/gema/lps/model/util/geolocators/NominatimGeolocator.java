/*% if (feature.DM_A_G_Nominatim) { %*/
package es.udc.lbd.gema.lps.model.util.geolocators;

import es.udc.lbd.gema.lps.model.util.geolocators.NominatimJSON.NominatimResultJSON;

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


public class NominatimGeolocator {
    private static final Logger logger = LoggerFactory.getLogger(NominatimGeolocator.class);
    private static final int SRID = /*%= data.basicData.SRID || 4326  %*/;

    public static Point geolocate(String searchString) {
        return NominatimGeolocator.geolocate(Arrays.asList(searchString));
    }

    /**
     * Geolocates using Nominatim service
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
                URIBuilder u = new URIBuilder("https://nominatim.openstreetmap.org/search");
                u.addParameter("q", StringUtils.join(fields, ","));
                u.addParameter("format", "json");
                u.addParameter("limit", "1");
                URI uri = u.build().toURL().toURI();
                logger.debug("URI: {}", uri);

                ClientResponse response = client.resource(uri).accept("application/json").get(ClientResponse.class);

                if (response.getStatus() != 200) {
                    logger.error("Error retrieving geolocation from Nominatim. HTTP Error code: " + response.getStatus());
                }

                NominatimResultJSON[] nominatimJSON = new ObjectMapper().readValue(response.getEntity(String.class), NominatimResultJSON[].class);

                if (nominatimJSON.length > 0) {
                    // If NOT empty results: Use the FIRST result
                    NominatimResultJSON location = nominatimJSON[0];

                    Double lng = Double.valueOf(location.getLon());
                    Double lat = Double.valueOf(location.getLat());

                    // PostGIS: x-> Longitude, y-> Latitude
                    cs.setOrdinate(0, 0, lng);
                    cs.setOrdinate(0, 1, lat);

                    logger.debug("Lat: {}, Lon: {} ", lat, lng);

                    return new Point(cs, gf);
                } else {
                    fields.remove(fields.size() - 1);
                }
            }
        } catch (Exception e) {
            logger.error("Error geolocating entity via Nominatim", e);
        }
        return null;
    }
}
/*% } %*/
