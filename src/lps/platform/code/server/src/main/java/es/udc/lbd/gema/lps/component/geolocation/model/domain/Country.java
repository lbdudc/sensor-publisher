/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.model.domain;
/*% if (feature.T_EntitiesInformation) { %*/
import es.udc.lbd.gema.lps.component.entities_information.EntityInfo;
/*% } %*/

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
/*% if (feature.T_EntitiesInformation) { %*/
@EntityInfo(hidden = true)
/*% } %*/
@DiscriminatorValue("COUNTRY")
public class Country extends Geoname {

    @Column(name = "country")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
/*% } %*/
