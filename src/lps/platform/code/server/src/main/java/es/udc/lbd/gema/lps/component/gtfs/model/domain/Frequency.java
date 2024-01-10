/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_frequencies")
@Table(name = "t_frequencies")
public class Frequency {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "frequenciesid")
  @SequenceGenerator(
      name = "frequenciesid",
      sequenceName = "t_frequencies_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @JsonView(Views.General.class)
  @Column(name = "id", unique = true)
  private Long id;

  @JsonView(Views.General.class)
  @Column(name = "start_time")
  private String startTime;

  @JsonView(Views.General.class)
  @Column(name = "end_time")
  private String endTime;

  @JsonView(Views.General.class)
  @Column(name = "headway_secs")
  private String headwaySecs;

  @JsonView(Views.General.class)
  @Column(name = "exact_times")
  private Integer exactTimes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id")
  @JsonView(Views.General.class)
  private Trip trip;

  public Frequency() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getHeadwaySecs() {
    return headwaySecs;
  }

  public void setHeadwaySecs(String headwaySecs) {
    this.headwaySecs = headwaySecs;
  }

  public Integer getExactTimes() {
    return exactTimes;
  }

  public void setExactTimes(Integer exactTimes) {
    this.exactTimes = exactTimes;
  }

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }
}
/*% } %*/
