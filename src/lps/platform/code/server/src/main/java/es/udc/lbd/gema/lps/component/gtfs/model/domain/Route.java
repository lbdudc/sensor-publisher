/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_routes")
@Table(name = "t_routes")
public class Route {
  @Id
  @JsonView(Views.General.class)
  @Column(name = "route_id", unique = true)
  private String routeId;

  @JsonView(Views.General.class)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agency_id")
  private Agency agency;

  @JsonView(Views.General.class)
  @Column(name = "route_short_name")
  private String routeShortName;

  @JsonView(Views.General.class)
  @Column(name = "route_long_name")
  private String routeLongName;

  @JsonView(Views.General.class)
  @Column(name = "route_desc")
  private String routeDesc;

  @JsonView(Views.General.class)
  @Column(name = "route_type")
  private Integer routeType;

  @JsonView(Views.General.class)
  @Column(name = "route_url")
  private String routeUrl;

  @JsonView(Views.General.class)
  @Column(name = "route_color")
  private String routeColor;

  @JsonView(Views.General.class)
  @Column(name = "route_text_color")
  private String routeTextColor;

  @JsonView(Views.General.class)
  @Column(name = "route_sort_order")
  private Integer routeSortOrder;

  @OneToMany(mappedBy = "route")
  @JsonIgnore
  private List<Trip> trips;

  public Route() {}

  public String getRouteId() {
    return routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public Agency getAgency() {
    return agency;
  }

  public void setAgency(Agency agency) {
    this.agency = agency;
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

  public List<Trip> getTrips() {
    return trips;
  }

  public void setTrips(List<Trip> trips) {
    this.trips = trips;
  }
}
/*% } %*/
