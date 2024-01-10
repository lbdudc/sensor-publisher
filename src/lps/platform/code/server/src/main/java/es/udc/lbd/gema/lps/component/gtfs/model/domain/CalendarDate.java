/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import java.time.LocalDate;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_calendar_dates")
@Table(name = "t_calendar_dates")
public class CalendarDate {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendarDatesid")
  @SequenceGenerator(
      name = "calendarDatesid",
      sequenceName = "t_calendar_dates_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @JsonView(Views.General.class)
  @Column(name = "id", unique = true)
  private Long id;

  @JsonView(Views.General.class)
  @Column(name = "date")
  private LocalDate date;

  @JsonView(Views.General.class)
  @Column(name = "exception_type")
  private Integer exceptionType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_id")
  @JsonView(Views.General.class)
  private Calendar service;

  public CalendarDate() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Calendar getService() {
    return service;
  }

  public void setService(Calendar service) {
    this.service = service;
  }
}
/*% } %*/
