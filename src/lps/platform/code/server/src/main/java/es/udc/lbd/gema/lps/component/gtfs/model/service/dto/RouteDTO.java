/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Route;
import es.udc.lbd.gema.lps.model.domain.*;

public class RouteDTO {

  private String routeId;
  private String routeShortName;
  private String routeLongName;
  private String routeDesc;
  private Integer routeType;
  private String routeUrl;
  private String routeColor;
  private String routeTextColor;
  private Integer routeSortOrder;
  private AgencyDTO agency;

  public RouteDTO() {}

  public RouteDTO(Route route) {
    this.routeId = route.getRouteId();
    this.routeShortName = route.getRouteShortName();
    this.routeLongName = route.getRouteLongName();
    this.routeDesc = route.getRouteDesc();
    this.routeType = route.getRouteType();
    this.routeUrl = route.getRouteUrl();
    this.routeColor = route.getRouteColor();
    this.routeTextColor = route.getRouteTextColor();
    this.routeSortOrder = route.getRouteSortOrder();
    if (route.getAgency() != null) {
      this.agency = new AgencyDTO(route.getAgency());
    }
  }

  public String getRouteId() {
    return routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public String getRouteShortName() {
    return routeShortName;
  }

  public void setRouteShortName(String routeShortName) {
    this.routeShortName = routeShortName;
  }

  public String getRouteLongName() {
    return routeLongName;
  }

  public void setRouteLongName(String routeLongName) {
    this.routeLongName = routeLongName;
  }

  public String getRouteDesc() {
    return routeDesc;
  }

  public void setRouteDesc(String routeDesc) {
    this.routeDesc = routeDesc;
  }

  public Integer getRouteType() {
    return routeType;
  }

  public void setRouteType(Integer routeType) {
    this.routeType = routeType;
  }

  public String getRouteUrl() {
    return routeUrl;
  }

  public void setRouteUrl(String routeUrl) {
    this.routeUrl = routeUrl;
  }

  public String getRouteColor() {
    return routeColor;
  }

  public void setRouteColor(String routeColor) {
    this.routeColor = routeColor;
  }

  public String getRouteTextColor() {
    return routeTextColor;
  }

  public void setRouteTextColor(String routeTextColor) {
    this.routeTextColor = routeTextColor;
  }

  public Integer getRouteSortOrder() {
    return routeSortOrder;
  }

  public void setRouteSortOrder(Integer routeSortOrder) {
    this.routeSortOrder = routeSortOrder;
  }

  public AgencyDTO getAgency() {
    return agency;
  }

  public void setAgency(AgencyDTO agency) {
    this.agency = agency;
  }

  public Route toRoute() {
    Route route = new Route();
    route.setRouteId(this.getRouteId());
    route.setRouteShortName(this.getRouteShortName());
    route.setRouteLongName(this.getRouteLongName());
    route.setRouteDesc(this.getRouteDesc());
    route.setRouteType(this.getRouteType());
    route.setRouteUrl(this.getRouteUrl());
    route.setRouteColor(this.getRouteColor());
    route.setRouteTextColor(this.getRouteTextColor());
    route.setRouteSortOrder(this.getRouteSortOrder());
    if (this.getAgency() != null) {
      route.setAgency(this.getAgency().toAgency());
    }
    return route;
  }
}
/*% } %*/
