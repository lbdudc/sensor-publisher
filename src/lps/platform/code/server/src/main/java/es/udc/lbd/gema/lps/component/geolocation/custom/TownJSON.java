/*% if (feature.DM_DS_Address) { %*/
package es.udc.lbd.gema.lps.component.geolocation.custom;

public class TownJSON {

    private int id;
    private String type;
    private String name;

    public TownJSON() {
    }

    public TownJSON(int id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

}
/*% } %*/
