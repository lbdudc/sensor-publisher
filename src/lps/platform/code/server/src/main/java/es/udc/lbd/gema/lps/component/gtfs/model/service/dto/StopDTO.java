/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Stop;
import es.udc.lbd.gema.lps.model.domain.*;
import java.util.List;
import java.util.stream.Collectors;

public class StopDTO {

  private Long id;
  private String stopId;
  private String stopCode;
  private String stopName;
  private String stopDesc;
  private String zoneId;
  private String stopUrl;
  private Integer locationType;
  private String parentStation;
  private String stopTimezone;
  private Integer wheelchairBoarding;
  private String levelId;
  private String platformCode;
  private StopDTO parent;
  private List<StopDTO> children;

  public StopDTO() {}

  public StopDTO(Stop stop) {
    this.stopId = stop.getStopId();
    this.stopCode = stop.getStopCode();
    this.stopName = stop.getStopName();
    this.stopDesc = stop.getStopDesc();
    this.zoneId = stop.getZoneId();
    this.stopUrl = stop.getStopUrl();
    this.locationType = stop.getLocationType();
    this.parentStation = stop.getParentStation();
    this.stopTimezone = stop.getStopTimezone();
    this.wheelchairBoarding = stop.getWheelchairBoarding();
    this.levelId = stop.getLevelId();
    this.platformCode = stop.getPlatformCode();
    if (stop.getParent() != null) {
      this.parent = new StopDTO(stop.getParent());
    }
    if (stop.getChildren() != null) {
      this.children = stop.getChildren().stream().map(StopDTO::new).collect(Collectors.toList());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public StopDTO getParent() {
    return parent;
  }

  public void setParent(StopDTO parent) {
    this.parent = parent;
  }

  public List<StopDTO> getChildren() {
    return children;
  }

  public void setChildren(List<StopDTO> children) {
    this.children = children;
  }

  public Stop toStop() {
    Stop stop = new Stop();
    stop.setStopId(this.getStopId());
    stop.setStopCode(this.getStopCode());
    stop.setStopName(this.getStopName());
    stop.setStopDesc(this.getStopDesc());
    stop.setZoneId(this.getZoneId());
    stop.setStopUrl(this.getStopUrl());
    stop.setLocationType(this.getLocationType());
    stop.setParentStation(this.getParentStation());
    stop.setStopTimezone(this.getStopTimezone());
    stop.setWheelchairBoarding(this.getWheelchairBoarding());
    stop.setLevelId(this.getLevelId());
    stop.setPlatformCode(this.getPlatformCode());
    if (this.getParent() != null) {
      stop.setParent(this.getParent().toStop());
    }
    if (this.getChildren() != null) {
      stop.setChildren(
          this.getChildren().stream().map(StopDTO::toStop).collect(Collectors.toList()));
    }
    return stop;
  }
}
/*% } %*/
