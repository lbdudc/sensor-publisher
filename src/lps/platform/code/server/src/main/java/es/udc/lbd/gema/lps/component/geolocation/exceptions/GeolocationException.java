/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.exceptions;

import es.udc.lbd.gema.lps.model.service.exceptions.AppException;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public abstract class GeolocationException extends AppException {

    protected static final Logger logger = LoggerFactory.getLogger(GeolocationException.class);

    public GeolocationException(String errorCode, HttpStatus status) {
        super(errorCode, status);
    }

    public GeolocationException(String errorCode, String parameter, HttpStatus status) {
        super(errorCode, parameter, status);
    }

    public GeolocationException(String errorCode, Map<String, String> parameters, HttpStatus status) {
        super(errorCode, parameters, status);
    }
}
/*% } %*/
