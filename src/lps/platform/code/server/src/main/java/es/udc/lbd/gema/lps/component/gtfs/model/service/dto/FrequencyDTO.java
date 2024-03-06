/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Frequency;
import es.udc.lbd.gema.lps.model.domain.*;

public class FrequencyDTO {

  private Long id;
  private String startTime;
  private String endTime;
  private String headwaySecs;
  private Integer exactTimes;
  private TripDTO trip;

  public FrequencyDTO() {}

  public FrequencyDTO(Frequency frecuency) {
    this.id = frecuency.getId();
    this.startTime = frecuency.getStartTime();
    this.endTime = frecuency.getEndTime();
    this.headwaySecs = frecuency.getHeadwaySecs();
    this.exactTimes = frecuency.getExactTimes();
    if (frecuency.getTrip() != null) {
      this.trip = new TripDTO(frecuency.getTrip());
    }
  }

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

  public TripDTO getTrip() {
    return trip;
  }

  public void setTrip(TripDTO trip) {
    this.trip = trip;
  }

  public Frequency toFrequency() {
    Frequency frecuency = new Frequency();
    frecuency.setId(this.getId());
    frecuency.setStartTime(this.getStartTime());
    frecuency.setEndTime(this.getEndTime());
    frecuency.setHeadwaySecs(this.getHeadwaySecs());
    frecuency.setExactTimes(this.getExactTimes());
    if (this.getTrip() != null) {
      frecuency.setTrip(this.getTrip().toTrip());
    }
    return frecuency;
  }
}
/*% } %*/
