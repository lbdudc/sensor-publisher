/*% if (feature.MWM_M_Client) { %*/
package es.udc.lbd.gema.lps.component.gema.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "te_client")
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clientGen")
  @SequenceGenerator(name = "clientGen", sequenceName = "te_client_id_seq", allocationSize = 1)
  private Long id;

  private String label;

  @Column(nullable = false)
  private String fullName;
  private String email;
  private String phone;
  private String address;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  @Column(columnDefinition = "geometry(Point, /*%= data.basicData.SRID || 4326  %*/)")
  private Point location;

  @Column(nullable = false)
  private Boolean active;

  public Client() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}
/*% } %*/
