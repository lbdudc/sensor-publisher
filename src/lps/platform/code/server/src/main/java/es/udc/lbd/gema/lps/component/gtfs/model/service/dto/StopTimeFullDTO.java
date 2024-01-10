/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.StopTime;
import es.udc.lbd.gema.lps.model.domain.*;

public class StopTimeFullDTO {
  private Long id;
  private String arrivalTime;
  private String departureTime;
  private Integer stopSequence;
  private Integer stopSequenceConsec;
  private String stopHeadsign;
  private Integer pickupType;
  private Integer dropOffType;
  private Float shapeDistTraveled;
  private Integer timepoint;
  private TripDTO trip;
  private StopDTO stop;

  public StopTimeFullDTO() {}

  public StopTimeFullDTO(StopTime stopTime) {
    this.id = stopTime.getId();
    this.arrivalTime = stopTime.getArrivalTime();
    this.departureTime = stopTime.getDepartureTime();
    this.stopSequence = stopTime.getStopSequence();
    this.stopSequenceConsec = stopTime.getStopSequenceConsec();
    this.stopHeadsign = stopTime.getStopHeadsign();
    this.pickupType = stopTime.getPickupType();
    this.dropOffType = stopTime.getDropOffType();
    this.shapeDistTraveled = stopTime.getShapeDistTraveled();
    this.timepoint = stopTime.getTimepoint();
    if (stopTime.getTrip() != null) {
      this.trip = new TripDTO(stopTime.getTrip());
    }
    if (stopTime.getStop() != null) {
      this.stop = new StopDTO(stopTime.getStop());
    }
  }

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

  public TripDTO getTrip() {
    return trip;
  }

  public void setTrip(TripDTO trip) {
    this.trip = trip;
  }

  public StopDTO getStop() {
    return stop;
  }

  public void setStop(StopDTO stop) {
    this.stop = stop;
  }

  public StopTime toStopTime() {
    StopTime stopTime = new StopTime();
    stopTime.setId(this.getId());
    stopTime.setArrivalTime(this.getArrivalTime());
    stopTime.setDepartureTime(this.getDepartureTime());
    stopTime.setStopSequence(this.getStopSequence());
    stopTime.setStopSequenceConsec(this.getStopSequenceConsec());
    stopTime.setStopHeadsign(this.getStopHeadsign());
    stopTime.setPickupType(this.getPickupType());
    stopTime.setDropOffType(this.getDropOffType());
    stopTime.setShapeDistTraveled(this.getShapeDistTraveled());
    stopTime.setTimepoint(this.getTimepoint());
    if (this.getTrip() != null) {
      stopTime.setTrip(this.getTrip().toTrip());
    }
    if (this.getStop() != null) {
      stopTime.setStop(this.getStop().toStop());
    }
    return stopTime;
  }
}
/*% } %*/
