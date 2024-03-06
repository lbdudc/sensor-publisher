/*% if (feature.MWM_M_Employee) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javax.validation.constraints.NotEmpty;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import org.locationtech.jts.geom.Point;

public class EmployeeDTO {

  private Long id;

  @NotEmpty
  private String fullName;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Point location;

  private Boolean active;

  public EmployeeDTO() {}

  public EmployeeDTO(Employee employee) {
    this.id = employee.getId();
    this.fullName = employee.getFullName();
    this.location = employee.getLocation();
    this.active = employee.getActive();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Point getLocation() {
    return location;
  }

  public void setLocation(Point location) {
    this.location = location;
  }

public Boolean getActive() {
	return active;
}

public void setActive(Boolean active) {
	this.active = active;
}
}
/*% } %*/
