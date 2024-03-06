/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Calendar;
import es.udc.lbd.gema.lps.model.domain.*;
import java.time.LocalDate;

public class CalendarFullDTO {
  private Long id;
  private String serviceId;
  private Integer monday;
  private Integer tuesday;
  private Integer wednesday;
  private Integer thursday;
  private Integer friday;
  private Integer saturday;
  private Integer sunday;
  private LocalDate startDate;
  private LocalDate endDate;

  public CalendarFullDTO() {}

  public CalendarFullDTO(Calendar calendar) {
    this.serviceId = calendar.getServiceId();
    this.monday = calendar.getMonday();
    this.tuesday = calendar.getTuesday();
    this.wednesday = calendar.getWednesday();
    this.thursday = calendar.getThursday();
    this.friday = calendar.getFriday();
    this.saturday = calendar.getSaturday();
    this.sunday = calendar.getSunday();
    this.startDate = calendar.getStartDate();
    this.endDate = calendar.getEndDate();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Calendar toCalendar() {
    Calendar calendar = new Calendar();
    calendar.setServiceId(this.getServiceId());
    calendar.setMonday(this.getMonday());
    calendar.setTuesday(this.getTuesday());
    calendar.setWednesday(this.getWednesday());
    calendar.setThursday(this.getThursday());
    calendar.setFriday(this.getFriday());
    calendar.setSaturday(this.getSaturday());
    calendar.setSunday(this.getSunday());
    calendar.setStartDate(this.getStartDate());
    calendar.setEndDate(this.getEndDate());
    return calendar;
  }
}
/*% } %*/
