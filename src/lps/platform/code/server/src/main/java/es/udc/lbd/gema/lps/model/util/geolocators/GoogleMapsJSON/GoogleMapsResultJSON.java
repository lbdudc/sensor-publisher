/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsJSON;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsResultJSON {
    private GoogleMapsGeometryJSON geometry;

    public GoogleMapsGeometryJSON getGeometry() {
        return geometry;
    }

    public void setGeometry(GoogleMapsGeometryJSON geometry) {
        this.geometry = geometry;
    }
}
/*% } %*/
