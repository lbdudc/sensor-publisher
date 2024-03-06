/*% if (feature.MWM_M_PlannedVisit) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDate;
import java.time.LocalTime;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;

public class PlannedEventDTO {
  private Long id;

  private String description;
  private PlannedEventState state;
  private LocalDate date;
  /*% if (feature.MVM_VT_WithTime) { %*/
  private LocalTime startTime;
  private LocalTime endTime;
  /*% } %*/
  /*% if (feature.MVM_VT_OnlyDay) { %*/
  private Integer eventOrder;
  /*% } %*/
  /*% if (feature.MWM_M_Client) { %*/
  private ClientDTO client;
  /*% } %*/
  /*% if (feature.MWM_M_Employee) { %*/
  private EmployeeDTO employee;
  /*% } %*/

  private String address;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private MultiLineString route;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Geometry geom;

  public PlannedEventDTO() {}

  public PlannedEventDTO(PlannedEvent event/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, String detailUrl/*% } %*/) {
    this.id = event.getId();
    /*% if (feature.MWM_M_Client) { %*/

    // Check null value since is nullable
    if (event.getClient() != null) {
      this.client = new ClientDTO(event.getClient()/*% if (feature.MWM_TE_T_ClientCustomLink) { %*/, detailUrl/*% } %*/);
    }
    this.description = event.getDescription();
    /*% } %*/
    /*% if (feature.MWM_M_Employee) { %*/

    // Check null value since is nullable
    if (event.getEmployee() != null) {
      this.employee = new EmployeeDTO(event.getEmployee());
    }
    /*% } %*/

    this.geom = event.getGeom();
    this.route = event.getRoute();
    this.state = event.getState();
    this.date = event.getDate();
    this.address = event.getAddress();
    /*% if (feature.MVM_VT_WithTime) { %*/
    this.endTime = event.getEndTime();
    this.startTime = event.getStartTime();
    /*% } %*/
    /*% if (feature.MVM_VT_OnlyDay) { %*/
    this.eventOrder = event.getEventOrder();
    /*% } %*/
  }

  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  public PlannedEventDTO(PlannedEvent event) {
    this(event, null);
  }
  /*% } %*/

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
  /*% if (feature.MVM_VT_OnlyDay) { %*/
  public Integer getEventOrder() {
    return eventOrder;
  }

  public void setEventOrder(Integer eventOrder) {
    this.eventOrder = eventOrder;
  }
  /*% } %*/
  /*% if (feature.MWM_M_Client) { %*/

  public ClientDTO getClient() {
    return client;
  }

  public void setClient(ClientDTO client) {
    this.client = client;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

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
/*% } %*/
