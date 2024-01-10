/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "te_activity")
public class Activity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activityGen")
  @SequenceGenerator(name = "activityGen", sequenceName = "te_activity_id_seq", allocationSize = 1)
  private Long id;

  private LocalDateTime ts_init;
  private LocalDateTime ts_end;
  /*% if (feature.MWM_M_PlannedVisit) { %*/

  @ManyToOne private PlannedEvent event;
  /*% } %*/
  /*% if (feature.MWM_M_Employee) { %*/

  @ManyToOne(optional = false)
  private Employee employee;
  /*% } %*/

  @ManyToOne(optional = false)
  private ActivityCategory category;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Geometry geom;

  public Activity() {}

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

  public PlannedEvent getEvent() {
    return event;
  }

  public void setEvent(PlannedEvent event) {
    this.event = event;
  }
  /*% } %*/
  /*% if (feature.MWM_M_Employee) { %*/

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }
  /*% } %*/

  public Geometry getGeom() {
    return geom;
  }

  public void setGeom(Geometry geom) {
    this.geom = geom;
  }

  public ActivityCategory getCategory() {
    return category;
  }

  public void setCategory(ActivityCategory category) {
    this.category = category;
  }

  @Override
  public String toString() {
    return "Activity [id="
        + id
        + ", ts_init="
        + ts_init
        + ", ts_end="
        + ts_end
        + ", event="
        + event
        + ", employee="
        + employee
        + ", category="
        + category
        + ", geom="
        + geom
        + "]";
  }
}

/*% } %*/
