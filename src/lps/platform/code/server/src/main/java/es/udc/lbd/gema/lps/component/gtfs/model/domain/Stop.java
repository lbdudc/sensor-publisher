/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import es.udc.lbd.gema.lps.model.views.Views;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Column;
import org.locationtech.jts.geom.Point;

@Entity(name = "t_stops")
@Table(name = "t_stops")
public class Stop {
  @Id
  @JsonView(Views.General.class)
  @Column(name = "stop_id", unique = true)
  private String stopId;

  @JsonView(Views.General.class)
  @Column(name = "stop_code")
  private String stopCode;

  @JsonView(Views.General.class)
  @Column(name = "stop_name")
  private String stopName;

  @JsonView(Views.General.class)
  @Column(name = "stop_desc")
  private String stopDesc;

  @JsonView(Views.Detailed.class)
  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  @Column(name = "stop_loc", columnDefinition = "geometry(Point, 4326)")
  private Point stopLoc;

  @JsonView(Views.General.class)
  @Column(name = "zone_id")
  private String zoneId;

  @JsonView(Views.General.class)
  @Column(name = "stop_url")
  private String stopUrl;

  @JsonView(Views.General.class)
  @Column(name = "location_type")
  private Integer locationType;

  @JsonView(Views.General.class)
  @Column(name = "parent_station")
  private String parentStation;

  @JsonView(Views.General.class)
  @Column(name = "stop_timezone")
  private String stopTimezone;

  @JsonView(Views.General.class)
  @Column(name = "wheelchair_boarding")
  private Integer wheelchairBoarding;

  @JsonView(Views.General.class)
  @Column(name = "level_id")
  private String levelId;

  @JsonView(Views.General.class)
  @Column(name = "platform_code")
  private String platformCode;

  @OneToMany(mappedBy = "stop")
  @JsonIgnore
  private List<StopTime> stopTimes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent")
  @JsonView(Views.General.class)
  private Stop parent;

  @OneToMany(mappedBy = "parent")
  @JsonIgnore
  private List<Stop> children;

  public Stop() {}

  public String getStopId() {
    return stopId;
  }

  public void setStopId(String stopId) {
    this.stopId = stopId;
  }

  public String getStopCode() {
    return stopCode;
  }

  public void setStopCode(String stopCode) {
    this.stopCode = stopCode;
  }

  public String getStopName() {
    return stopName;
  }

  public void setStopName(String stopName) {
    this.stopName = stopName;
  }

  public String getStopDesc() {
    return stopDesc;
  }

  public void setStopDesc(String stopDesc) {
    this.stopDesc = stopDesc;
  }

  public Point getStopLoc() {
    return stopLoc;
  }

  public void setStopLoc(Point stopLoc) {
    this.stopLoc = stopLoc;
  }

  public String getZoneId() {
    return zoneId;
  }

  public void setZoneId(String zoneId) {
    this.zoneId = zoneId;
  }

  public String getStopUrl() {
    return stopUrl;
  }

  public void setStopUrl(String stopUrl) {
    this.stopUrl = stopUrl;
  }

  public Integer getLocationType() {
    return locationType;
  }

  public void setLocationType(Integer locationType) {
    this.locationType = locationType;
  }

  public String getParentStation() {
    return parentStation;
  }

  public void setParentStation(String parentStation) {
    this.parentStation = parentStation;
  }

  public String getStopTimezone() {
    return stopTimezone;
  }

  public void setStopTimezone(String stopTimezone) {
    this.stopTimezone = stopTimezone;
  }

  public Integer getWheelchairBoarding() {
    return wheelchairBoarding;
  }

  public void setWheelchairBoarding(Integer wheelchairBoarding) {
    this.wheelchairBoarding = wheelchairBoarding;
  }

  public String getLevelId() {
    return levelId;
  }

  public void setLevelId(String levelId) {
    this.levelId = levelId;
  }

  public String getPlatformCode() {
    return platformCode;
  }

  public void setPlatformCode(String platformCode) {
    this.platformCode = platformCode;
  }

  public List<StopTime> getStopTimes() {
    return stopTimes;
  }

  public void setStopTimes(List<StopTime> stopTimes) {
    this.stopTimes = stopTimes;
  }

  public Stop getParent() {
    return parent;
  }

  public void setParent(Stop parent) {
    this.parent = parent;
  }

  public List<Stop> getChildren() {
    return children;
  }

  public void setChildren(List<Stop> children) {
    this.children = children;
  }
}
/*% } %*/
