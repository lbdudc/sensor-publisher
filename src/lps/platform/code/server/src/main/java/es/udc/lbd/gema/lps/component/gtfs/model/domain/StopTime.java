/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_stop_times")
@Table(name = "t_stop_times")
public class StopTime {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stopTimesid")
  @SequenceGenerator(
      name = "stopTimesid",
      sequenceName = "t_stop_times_id_seq",
      initialValue = 1,
      allocationSize = 1)
  @JsonView(Views.General.class)
  @Column(name = "id", unique = true)
  private Long id;

  @JsonView(Views.General.class)
  @Column(name = "arrival_time")
  private String arrivalTime;

  @JsonView(Views.General.class)
  @Column(name = "departure_time")
  private String departureTime;

  @JsonView(Views.General.class)
  @Column(name = "stop_sequence")
  private Integer stopSequence;

  @JsonView(Views.General.class)
  @Column(name = "stop_sequence_consec")
  private Integer stopSequenceConsec;

  @JsonView(Views.General.class)
  @Column(name = "stop_headsign")
  private String stopHeadsign;

  @JsonView(Views.General.class)
  @Column(name = "pickup_type")
  private Integer pickupType;

  @JsonView(Views.General.class)
  @Column(name = "drop_off_type")
  private Integer dropOffType;

  @JsonView(Views.General.class)
  @Column(name = "shape_dist_traveled")
  private Float shapeDistTraveled;

  @JsonView(Views.General.class)
  @Column(name = "timepoint")
  private Integer timepoint;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id")
  @JsonView(Views.General.class)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "stop_id")
  @JsonView(Views.General.class)
  private Stop stop;

  public StopTime() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(String arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public String getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(String departureTime) {
    this.departureTime = departureTime;
  }

  public Integer getStopSequence() {
    return stopSequence;
  }

  public void setStopSequence(Integer stopSequence) {
    this.stopSequence = stopSequence;
  }

  public Integer getStopSequenceConsec() {
    return stopSequenceConsec;
  }

  public void setStopSequenceConsec(Integer stopSequenceConsec) {
    this.stopSequenceConsec = stopSequenceConsec;
  }

  public String getStopHeadsign() {
    return stopHeadsign;
  }

  public void setStopHeadsign(String stopHeadsign) {
    this.stopHeadsign = stopHeadsign;
  }

  public Integer getPickupType() {
    return pickupType;
  }

  public void setPickupType(Integer pickupType) {
    this.pickupType = pickupType;
  }

  public Integer getDropOffType() {
    return dropOffType;
  }

  public void setDropOffType(Integer dropOffType) {
    this.dropOffType = dropOffType;
  }

  public Float getShapeDistTraveled() {
    return shapeDistTraveled;
  }

  public void setShapeDistTraveled(Float shapeDistTraveled) {
    this.shapeDistTraveled = shapeDistTraveled;
  }

  public Integer getTimepoint() {
    return timepoint;
  }

  public void setTimepoint(Integer timepoint) {
    this.timepoint = timepoint;
  }

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }

  public Stop getStop() {
    return stop;
  }

  public void setStop(Stop stop) {
    this.stop = stop;
  }
}
/*% } %*/
