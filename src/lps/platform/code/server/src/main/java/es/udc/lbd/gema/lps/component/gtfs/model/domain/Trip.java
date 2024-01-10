/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_trips")
@Table(name = "t_trips")
public class Trip {
  @Id
  @JsonView(Views.General.class)
  @Column(name = "trip_id", unique = true)
  private String tripId;

  @JsonView(Views.General.class)
  @Column(name = "trip_headsign")
  private String tripHeadsign;

  @JsonView(Views.General.class)
  @Column(name = "trip_short_name")
  private String tripShortName;

  @JsonView(Views.General.class)
  @Column(name = "direction_id")
  private Integer directionId;

  @JsonView(Views.General.class)
  @Column(name = "block_id")
  private String blockId;

  @JsonView(Views.General.class)
  @Column(name = "wheelchair_accessible")
  private Integer wheelchairAccessible;

  @JsonView(Views.General.class)
  @Column(name = "bikes_allowed")
  private Integer bikesAllowed;

  @JsonView(Views.General.class)
  @Column(name = "shape_id")
  private String shapeId;

  @JsonView(Views.General.class)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id")
  private Route route;

  @OneToMany(mappedBy = "trip")
  @JsonIgnore
  private List<Frequency> frequencies;

  @OneToMany(mappedBy = "trip")
  @JsonIgnore
  private List<StopTime> stopTimes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "service_id")
  @JsonView(Views.General.class)
  private Calendar service;

  public Trip() {}

  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public String getTripHeadsign() {
    return tripHeadsign;
  }

  public void setTripHeadsign(String tripHeadsign) {
    this.tripHeadsign = tripHeadsign;
  }

  public String getTripShortName() {
    return tripShortName;
  }

  public void setTripShortName(String tripShortName) {
    this.tripShortName = tripShortName;
  }

  public Integer getDirectionId() {
    return directionId;
  }

  public void setDirectionId(Integer directionId) {
    this.directionId = directionId;
  }

  public String getBlockId() {
    return blockId;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  public Integer getWheelchairAccessible() {
    return wheelchairAccessible;
  }

  public void setWheelchairAccessible(Integer wheelchairAccessible) {
    this.wheelchairAccessible = wheelchairAccessible;
  }

  public Integer getBikesAllowed() {
    return bikesAllowed;
  }

  public void setBikesAllowed(Integer bikesAllowed) {
    this.bikesAllowed = bikesAllowed;
  }

  public String getShapeId() {
    return shapeId;
  }

  public void setShapeId(String shape) {
    this.shapeId = shape;
  }

  public List<Frequency> getFrequencies() {
    return frequencies;
  }

  public void setFrequencies(List<Frequency> frequencies) {
    this.frequencies = frequencies;
  }

  public List<StopTime> getStopTimes() {
    return stopTimes;
  }

  public void setStopTimes(List<StopTime> stopTimes) {
    this.stopTimes = stopTimes;
  }

  public Calendar getService() {
    return service;
  }

  public void setService(Calendar service) {
    this.service = service;
  }

  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }
}
/*% } %*/
