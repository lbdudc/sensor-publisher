/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_calendar")
@Table(name = "t_calendar")
public class Calendar {
  @Id
  @JsonView(Views.General.class)
  @Column(name = "service_id", unique = true)
  private String serviceId;

  @JsonView(Views.General.class)
  @Column(name = "monday")
  private Integer monday;

  @JsonView(Views.General.class)
  @Column(name = "tuesday")
  private Integer tuesday;

  @JsonView(Views.General.class)
  @Column(name = "wednesday")
  private Integer wednesday;

  @JsonView(Views.General.class)
  @Column(name = "thursday")
  private Integer thursday;

  @JsonView(Views.General.class)
  @Column(name = "friday")
  private Integer friday;

  @JsonView(Views.General.class)
  @Column(name = "saturday")
  private Integer saturday;

  @JsonView(Views.General.class)
  @Column(name = "sunday")
  private Integer sunday;

  @JsonView(Views.General.class)
  @Column(name = "start_date")
  private LocalDate startDate;

  @JsonView(Views.General.class)
  @Column(name = "end_date")
  private LocalDate endDate;

  @OneToMany(mappedBy = "service")
  @JsonIgnore
  private List<CalendarDate> dates;

  @OneToMany(mappedBy = "service")
  @JsonIgnore
  private List<Trip> trips;

  public Calendar() {}

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public Integer getMonday() {
    return monday;
  }

  public void setMonday(Integer monday) {
    this.monday = monday;
  }

  public Integer getTuesday() {
    return tuesday;
  }

  public void setTuesday(Integer tuesday) {
    this.tuesday = tuesday;
  }

  public Integer getWednesday() {
    return wednesday;
  }

  public void setWednesday(Integer wednesday) {
    this.wednesday = wednesday;
  }

  public Integer getThursday() {
    return thursday;
  }

  public void setThursday(Integer thursday) {
    this.thursday = thursday;
  }

  public Integer getFriday() {
    return friday;
  }

  public void setFriday(Integer friday) {
    this.friday = friday;
  }

  public Integer getSaturday() {
    return saturday;
  }

  public void setSaturday(Integer saturday) {
    this.saturday = saturday;
  }

  public Integer getSunday() {
    return sunday;
  }

  public void setSunday(Integer sunday) {
    this.sunday = sunday;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public List<CalendarDate> getDates() {
    return dates;
  }

  public void setDates(List<CalendarDate> dates) {
    this.dates = dates;
  }

  public List<Trip> getTrips() {
    return trips;
  }

  public void setTrips(List<Trip> trips) {
    this.trips = trips;
  }
}
/*% } %*/
