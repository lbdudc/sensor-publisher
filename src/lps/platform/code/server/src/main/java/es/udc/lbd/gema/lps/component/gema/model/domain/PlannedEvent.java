/*% if (feature.MWM_M_PlannedVisit) { %*/
package es.udc.lbd.gema.lps.component.gema.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;

@Entity
@Table(name = "te_planned_event")
public class PlannedEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plannedEventGen")
  @SequenceGenerator(name = "plannedEventGen", sequenceName = "te_planned_event_id_seq", allocationSize = 1)
  private Long id;

  private String label;

  private String description;
  private PlannedEventState state;

  private String address;

  @Column(name = "date", columnDefinition = "DATE")
  private LocalDate date;
  /*% if (feature.MVM_VT_WithTime) { %*/

  @Column(name = "start_time", columnDefinition = "TIME")
  private LocalTime startTime;

  @Column(name = "end_time", columnDefinition = "TIME")
  private LocalTime endTime;
  /*% } %*/

  @Column(name = "realtime", columnDefinition = "TIME")
  private LocalTime realtime;
  /*% if (feature.MVM_VT_OnlyDay) { %*/

  private Integer eventOrder;
  /*% } %*/
  /*% if (feature.MWM_M_Client) { %*/

  @ManyToOne private Client client;
  /*% } %*/
  /*% if (feature.MWM_M_Employee) { %*/

  @ManyToOne private Employee employee;
  /*% } %*/
  /*% if (feature.MWM_M_Activity) { %*/

  @OneToMany(mappedBy = "event")
  @JsonIgnore
  private List<Activity> activities;
  /*% } %*/

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Geometry geom;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  @Column(columnDefinition = "geometry(MultiLineString, /*%= data.basicData.SRID || 4326  %*/)")
  private MultiLineString route;

  public PlannedEvent() {}

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PlannedEventState getState() {
    return state;
  }

  public void setState(PlannedEventState state) {
    this.state = state;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
  /*% if (feature.MVM_VT_WithTime) { %*/

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }
  /*% } %*/

  public LocalTime getRealtime() {
    return realtime;
  }

  public void setRealtime(LocalTime realtime) {
    this.realtime = realtime;
  }
  /*% if (feature.MVM_VT_OnlyDay) { %*/

  public Integer getEventOrder() {
    return eventOrder;
  }

  public void setEventOrder(Integer eventOrder) {
    this.eventOrder = eventOrder;
  }
  /*% } %*/
  /*% if (feature.MWM_M_Client) { %*/

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
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
  /*% if (feature.MWM_M_Activity) { %*/

  public List<Activity> getActivities() {
    return activities;
  }

  public void setActivities(List<Activity> activities) {
    this.activities = activities;
  }

  @PreRemove
  public void nullActivitiesReferences() {
    activities.forEach(act -> act.setEvent(null));
  }
  /*% } %*/

  public Geometry getGeom() {
    return geom;
  }

  public void setGeom(Geometry geom) {
    this.geom = geom;
  }

  public MultiLineString getRoute() {
    return route;
  }

  public void setRoute(MultiLineString route) {
    this.route = route;
  }

}
/*% } %*/
