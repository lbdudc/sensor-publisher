/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Trip;
import es.udc.lbd.gema.lps.model.domain.*;

public class TripFullDTO {
  private Long id;
  private String tripId;
  private String serviceId;
  private String tripHeadsign;
  private String tripShortName;
  private Integer directionId;
  private String blockId;
  private Integer wheelchairAccessible;
  private Integer bikesAllowed;
  private String shapeId;
  private String routeId;
  private CalendarDTO service;
  private RouteDTO route;

  public TripFullDTO() {}

  public TripFullDTO(Trip trip) {
    this.tripId = trip.getTripId();
    this.tripHeadsign = trip.getTripHeadsign();
    this.tripShortName = trip.getTripShortName();
    this.directionId = trip.getDirectionId();
    this.blockId = trip.getBlockId();
    this.wheelchairAccessible = trip.getWheelchairAccessible();
    this.bikesAllowed = trip.getBikesAllowed();
    this.shapeId = trip.getShapeId();
    if (trip.getService() != null) {
      this.service = new CalendarDTO(trip.getService());
    }
    if (trip.getRoute() != null) {
      this.route = new RouteDTO(trip.getRoute());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
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

  public void setShapeId(String shapeId) {
    this.shapeId = shapeId;
  }

  public String getRouteId() {
    return routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public CalendarDTO getService() {
    return service;
  }

  public void setService(CalendarDTO service) {
    this.service = service;
  }

  public RouteDTO getRoute() {
    return route;
  }

  public void setRoute(RouteDTO route) {
    this.route = route;
  }

  public Trip toTrip() {
    Trip trip = new Trip();
    trip.setTripId(this.getTripId());
    trip.setTripHeadsign(this.getTripHeadsign());
    trip.setTripShortName(this.getTripShortName());
    trip.setDirectionId(this.getDirectionId());
    trip.setBlockId(this.getBlockId());
    trip.setWheelchairAccessible(this.getWheelchairAccessible());
    trip.setBikesAllowed(this.getBikesAllowed());
    trip.setShapeId(this.getShapeId());
    if (this.getService() != null) {
      trip.setService(this.getService().toCalendar());
    }
    if (this.getRoute() != null) {
      trip.setRoute(this.getRoute().toRoute());
    }
    return trip;
  }
}
/*% } %*/
