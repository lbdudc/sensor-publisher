/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.locationtech.jts.geom.Geometry;

public class ActivityDTO {

  private Long id;

  private LocalDateTime ts_init;
  private LocalDateTime ts_end;
  /*% if (feature.MWM_M_PlannedVisit) { %*/

  private PlannedEventDTO event;
  /*% } %*/
  /*% if (feature.MWM_M_Employee) { %*/

  private EmployeeDTO employee;
  /*% } %*/

  private ActivityCategoryDTO category;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Geometry geom;

  private Double distance;

  private BigDecimal duration;

  public ActivityDTO() {}

  public ActivityDTO(Activity activity) {
    this.id = activity.getId();
    this.ts_init = activity.getTs_init();
    this.ts_end = activity.getTs_end();
    /*% if (feature.MWM_M_PlannedVisit) { %*/
    if (activity.getEvent() != null) {
      this.event = new PlannedEventDTO(activity.getEvent());
    }
    /*% } %*/
    /*% if (feature.MWM_M_Employee) { %*/
    this.employee = new EmployeeDTO(activity.getEmployee());
    /*% } %*/
    this.category = new ActivityCategoryDTO(activity.getCategory());
    this.geom = activity.getGeom();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getTs_init() {
    return ts_init;
  }

  public void setTs_init(LocalDateTime ts_init) {
    this.ts_init = ts_init;
  }

  public LocalDateTime getTs_end() {
    return ts_end;
  }

  public void setTs_end(LocalDateTime ts_end) {
    this.ts_end = ts_end;
  }

  /*% if (feature.MWM_M_PlannedVisit) { %*/
  public PlannedEventDTO getEvent() {
    return event;
  }

  public void setEvent(PlannedEventDTO event) {
    this.event = event;
  }
  /*% } %*/
  /*% if (feature.MWM_M_Employee) { %*/

  public EmployeeDTO getEmployee() {
    return employee;
  }

  public void setEmployee(EmployeeDTO employee) {
    this.employee = employee;
  }
  /*% } %*/

  public Geometry getGeom() {
    return geom;
  }

  public void setGeom(Geometry geom) {
    this.geom = geom;
  }

  public ActivityCategoryDTO getCategory() {
    return category;
  }

  public void setCategory(ActivityCategoryDTO category) {
    this.category = category;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public BigDecimal getDuration() {
    return duration;
  }

  public void setDuration(BigDecimal duration) {
    this.duration = duration;
  }
}
/*% } %*/
