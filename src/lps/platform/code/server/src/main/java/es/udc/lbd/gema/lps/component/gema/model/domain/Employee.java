/*% if (feature.MWM_M_Employee) { %*/
package es.udc.lbd.gema.lps.component.gema.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "te_employee")
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employeeGen")
  @SequenceGenerator(name = "employeeGen", sequenceName = "te_employee_id_seq", allocationSize = 1)
  private Long id;

  private String label;

  @Column(nullable = false)
  private String fullName;

  private String email;

  private String phone;

  private LocalDateTime lastProcessed;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  @Column(columnDefinition = "geometry(Point, /*%= data.basicData.SRID || 4326  %*/)")
  private Point location;

  @Column(nullable = false)
  private Boolean active;

  public Employee() {}

  public Employee(
      String label, String fullName, String email, String phone, Point location, Boolean active) {
    this.label = label;
    this.fullName = fullName;
    this.email = email;
    this.phone = phone;
    this.location = location;
    this.active = active;
  }

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

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

  public LocalDateTime getLastProcessed() {
    return lastProcessed;
  }

  public void setLastProcessed(LocalDateTime lastProcessed) {
    this.lastProcessed = lastProcessed;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  @Override
  public String toString() {
    return "Employee [id="
        + id
        + ", label="
        + label
        + ", fullName="
        + fullName
        + ", email="
        + email
        + ", phone="
        + phone
        + ", lastProcessed="
        + lastProcessed
        + ", location="
        + location
        + "]";
  }
}
/*% } %*/
