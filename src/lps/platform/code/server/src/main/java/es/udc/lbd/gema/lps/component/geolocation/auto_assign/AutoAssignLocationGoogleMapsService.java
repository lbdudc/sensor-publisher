/*% if (feature.DM_A_G_GoogleMaps) { %*/
package es.udc.lbd.gema.lps.component.geolocation.auto_assign;

import es.udc.lbd.gema.lps.component.geolocation.auto_assign.CommonJSON.GeolocationWsParams;
import es.udc.lbd.gema.lps.config.Properties;
import es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsGeolocator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.locationtech.jts.geom.Point;

@Service
public class AutoAssignLocationGoogleMapsService extends AutoAssignLocationService {

    private static final Logger logger = LoggerFactory.getLogger(AutoAssignLocationGoogleMapsService.class);

    private static final String GEOLOCATION_TYPE = "googlemaps";

    @Inject
    private Properties properties;

    /**
     * {@inheritDoc}
     */
    protected String getGeolocatorType() {
        return GEOLOCATION_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point geolocate(GeolocationWsParams data) {

        List<String> params = new ArrayList<String>();
        // @formatter:off
        if (StringUtils.isNotBlank(data.getAddress()     )) { params.add(data.getAddress()     ); }
        if (StringUtils.isNotBlank(data.getTown()        )) { params.add(data.getTown()        ); }
        if (StringUtils.isNotBlank(data.getSubdivision4())) { params.add(data.getSubdivision4()); }
        if (StringUtils.isNotBlank(data.getSubdivision3())) { params.add(data.getSubdivision3()); }
        if (StringUtils.isNotBlank(data.getSubdivision2())) { params.add(data.getSubdivision2()); }
        if (StringUtils.isNotBlank(data.getSubdivision1())) { params.add(data.getSubdivision1()); }
        if (StringUtils.isNotBlank(data.getCountry()     )) { params.add(data.getCountry()     ); }
        // @formatter:on

        return GoogleMapsGeolocator.geolocate(params);
    }
}
/*% } %*/
