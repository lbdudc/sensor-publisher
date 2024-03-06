/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.model.domain;
/*% if (feature.T_EntitiesInformation) { %*/
import es.udc.lbd.gema.lps.component.entities_information.EntityInfo;
/*% } %*/
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Immutable
@Table(schema = "geonames", name = "geoname")
/*% if (feature.T_EntitiesInformation) { %*/
@EntityInfo(hidden = true)
/*% } %*/
@DiscriminatorColumn(name = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Country.class, name = "COUNTRY"),
    @JsonSubTypes.Type(value = Subdivision1.class, name = "ADM1"),
    @JsonSubTypes.Type(value = Subdivision2.class, name = "ADM2"),
    @JsonSubTypes.Type(value = Subdivision3.class, name = "ADM3"),
    @JsonSubTypes.Type(value = Subdivision4.class, name = "ADM4"),
    @JsonSubTypes.Type(value = Town.class, name = "TOWN")
})
public abstract class Geoname implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "geonameGen")
    @SequenceGenerator(name = "geonameGen", sequenceName = "geoname_geonameid_seq", allocationSize = 1)
    @Column(name = "geonameid")
    private Integer id;

    public Geoname() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
/*% } %*/
