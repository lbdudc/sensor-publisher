/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.exceptions.client;

import es.udc.lbd.gema.lps.component.geolocation.exceptions.GeolocationException;

import org.springframework.http.HttpStatus;

public class GeolocatorNotSupportedException extends GeolocationException {

    public GeolocatorNotSupportedException() {
        super("geolocation.autoassign_not_supported", HttpStatus.BAD_REQUEST);
        logger.warn("Geolocation: Geolocator not supported");
    }

}
/*% } %*/
