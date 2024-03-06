/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsJSON;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsLocationJSON {
    Double lat;
    Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
/*% } %*/
