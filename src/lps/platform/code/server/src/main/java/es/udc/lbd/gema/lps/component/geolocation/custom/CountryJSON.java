/*% if (feature.DM_DS_Address) { %*/
package es.udc.lbd.gema.lps.component.geolocation.custom;

public class CountryJSON {

    private Integer id;
    private String code;
    private String type;
    private String name;

    public CountryJSON() {
    }

    public CountryJSON(Integer id, String code, String type,  String name) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
/*% } %*/
