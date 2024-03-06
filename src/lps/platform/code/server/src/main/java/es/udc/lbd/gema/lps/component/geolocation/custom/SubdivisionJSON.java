/*% if (feature.DM_DS_Address) { %*/
package es.udc.lbd.gema.lps.component.geolocation.custom;

public class SubdivisionJSON {

    private int id;
    private String type;
    private String code;
    private String name;

    public SubdivisionJSON() {
    }

    public SubdivisionJSON(int id, String type, String code, String name) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
/*% } %*/
