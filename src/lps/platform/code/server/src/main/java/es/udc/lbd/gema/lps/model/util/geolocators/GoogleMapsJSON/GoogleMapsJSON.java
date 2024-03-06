/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.model.util.geolocators.GoogleMapsJSON;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsJSON {
    private List<GoogleMapsResultJSON> results;
    private String status;
    private String error_message;

    public List<GoogleMapsResultJSON> getResults() {
        return results;
    }

    public void setResults(List<GoogleMapsResultJSON> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}
/*% } %*/
