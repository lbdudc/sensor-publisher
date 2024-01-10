/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.custom;

public class AutoAssignResultJSON {
    int geolocated;
    int notGeolocated;
    int ignored;

    public AutoAssignResultJSON() {
    }

    public int getGeolocated() {
        return geolocated;
    }

    public void setGeolocated(int geolocated) {
        this.geolocated = geolocated;
    }

    /**
     * Increases geolocated counter by one
     */
    public void addGeolocated() {
        this.geolocated++;
    }

    public int getNotGeolocated() {
        return notGeolocated;
    }

    public void setNotGeolocated(int notGeolocated) {
        this.notGeolocated = notGeolocated;
    }

    /**
     * Increases not geolocated counter by one
     */
    public void addNotGeolocated() {
        this.notGeolocated++;
    }

    public int getIgnored() {
        return ignored;
    }

    public void setIgnored(int ignored) {
        this.ignored = ignored;
    }

    /**
     * Increases ignored counter by one
     */
    public void addIgnored() {
        this.ignored++;
    }
}
/*% } %*/
