/*% if (feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.auto_assign.CommonJSON;

public class GeolocationWsParams {
    private String address;
    private String country;
    private String subdivision1;
    private String subdivision2;
    private String subdivision3;
    private String subdivision4;
    private String town;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSubdivision1() {
        return subdivision1;
    }

    public void setSubdivision1(String subdivision1) {
        this.subdivision1 = subdivision1;
    }

    public String getSubdivision2() {
        return subdivision2;
    }

    public void setSubdivision2(String subdivision2) {
        this.subdivision2 = subdivision2;
    }

    public String getSubdivision3() {
        return subdivision3;
    }

    public void setSubdivision3(String subdivision3) {
        this.subdivision3 = subdivision3;
    }

    public String getSubdivision4() {
        return subdivision4;
    }

    public void setSubdivision4(String subdivision4) {
        this.subdivision4 = subdivision4;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "GoogleMapsWsParams{" +
            "address='" + address + '\'' +
            ", country='" + country + '\'' +
            ", subdivision1='" + subdivision1 + '\'' +
            ", subdivision2='" + subdivision2 + '\'' +
            ", subdivision3='" + subdivision3 + '\'' +
            ", subdivision4='" + subdivision4 + '\'' +
            ", town='" + town + '\'' +
            '}';
    }
}
/*% } %*/
