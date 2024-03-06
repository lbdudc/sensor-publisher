/*% if (feature.DM_DS_Address || feature.DM_A_G_Batch) { %*/
package es.udc.lbd.gema.lps.component.geolocation.model.domain;
/*% if (feature.T_EntitiesInformation) { %*/
import es.udc.lbd.gema.lps.component.entities_information.EntityInfo;
/*% } %*/
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Point;

@Entity
/*% if (feature.T_EntitiesInformation) { %*/
@EntityInfo(hidden = true)
/*% } %*/
@Table(name = "gc_address")
public class GCAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressGen")
    @SequenceGenerator(name = "addressGen", sequenceName = "gc_address_id_seq", allocationSize = 1)
    private Long id;

    private String line1;

    private String line2;

    private String zipCode;

    @OneToOne
    private Country country;

    @OneToOne
    private Subdivision1 subdivision1;

    @OneToOne
    private Subdivision2 subdivision2;

    @OneToOne
    private Subdivision3 subdivision3;

    @OneToOne
    private Subdivision4 subdivision4;

    @OneToOne
    private Town town;

    @JsonSerialize(using = CustomGeometrySerializer.class)
    @JsonDeserialize(using = CustomGeometryDeserializer.class)
    @Column(columnDefinition = "geometry(Point, /*%= data.basicData.SRID || 4326  %*/)")
    private Point point;

    public GCAddress() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Subdivision1 getSubdivision1() {
        return subdivision1;
    }

    public void setSubdivision1(Subdivision1 subdivision1) {
        this.subdivision1 = subdivision1;
    }

    public Subdivision2 getSubdivision2() {
        return subdivision2;
    }

    public void setSubdivision2(Subdivision2 subdivision2) {
        this.subdivision2 = subdivision2;
    }

    public Subdivision3 getSubdivision3() {
        return subdivision3;
    }

    public void setSubdivision3(Subdivision3 subdivision3) {
        this.subdivision3 = subdivision3;
    }

    public Subdivision4 getSubdivision4() {
        return subdivision4;
    }

    public void setSubdivision4(Subdivision4 subdivision4) {
        this.subdivision4 = subdivision4;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

}
/*% } %*/
