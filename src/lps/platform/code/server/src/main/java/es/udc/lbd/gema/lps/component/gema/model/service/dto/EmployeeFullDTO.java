/*% if (feature.MWM_M_Employee) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.validation.constraints.NotEmpty;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import org.locationtech.jts.geom.Point;

public class EmployeeFullDTO {

  private Long id;
  private String label;
  @NotEmpty
  private String fullName;
  private String email;
  private String phone;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point location;
  
  private Boolean active;

  public EmployeeFullDTO() {}

  public EmployeeFullDTO(Employee employee) {
    this.id = employee.getId();
    this.label = employee.getLabel();
    this.fullName = employee.getFullName();
    this.email = employee.getEmail();
    this.phone = employee.getPhone();
    this.location = employee.getLocation();
    this.active = employee.getActive();
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

  public Boolean getActive() {
	return active;
  }

  public void setActive(Boolean active) {
	this.active = active;
  }
}
/*% } %*/
