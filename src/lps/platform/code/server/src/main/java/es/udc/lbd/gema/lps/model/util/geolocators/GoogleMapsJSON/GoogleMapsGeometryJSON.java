/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsJSON;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsGeometryJSON {
    private GoogleMapsLocationJSON location;

    public GoogleMapsLocationJSON getLocation() {
        return location;
    }

    public void setLocation(GoogleMapsLocationJSON location) {
        this.location = location;
    }
}
/*% } %*/
