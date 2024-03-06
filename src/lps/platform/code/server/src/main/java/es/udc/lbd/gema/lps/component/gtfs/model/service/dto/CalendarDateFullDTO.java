/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.CalendarDate;
import es.udc.lbd.gema.lps.model.domain.*;
import java.time.LocalDate;

public class CalendarDateFullDTO {
  private Long id;
  private String serviceId;
  private LocalDate date;
  private Integer exceptionType;
  private CalendarDTO service;

  public CalendarDateFullDTO() {}

  public CalendarDateFullDTO(CalendarDate calendarDate) {
    this.id = calendarDate.getId();
    this.date = calendarDate.getDate();
    this.exceptionType = calendarDate.getExceptionType();
    if (calendarDate.getService() != null) {
      this.service = new CalendarDTO(calendarDate.getService());
    }
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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Integer getExceptionType() {
    return exceptionType;
  }

  public void setExceptionType(Integer exceptionType) {
    this.exceptionType = exceptionType;
  }

  public CalendarDTO getService() {
    return service;
  }

  public void setService(CalendarDTO service) {
    this.service = service;
  }

  public CalendarDate toCalendarDate() {
    CalendarDate calendarDate = new CalendarDate();
    calendarDate.setId(this.getId());
    calendarDate.setDate(this.getDate());
    calendarDate.setExceptionType(this.getExceptionType());
    if (this.getService() != null) {
      calendarDate.setService(this.getService().toCalendar());
    }
    return calendarDate;
  }
}
/*% } %*/
