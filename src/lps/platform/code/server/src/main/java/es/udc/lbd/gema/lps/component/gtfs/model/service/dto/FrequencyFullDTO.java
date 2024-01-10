/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Frequency;
import es.udc.lbd.gema.lps.model.domain.*;

public class FrequencyFullDTO {
  private Long id;
  private String startTime;
  private String endTime;
  private String headwaySecs;
  private Integer exactTimes;
  private TripDTO trip;

  public FrequencyFullDTO() {}

  public FrequencyFullDTO(Frequency frequency) {
    this.id = frequency.getId();
    this.startTime = frequency.getStartTime();
    this.endTime = frequency.getEndTime();
    this.headwaySecs = frequency.getHeadwaySecs();
    this.exactTimes = frequency.getExactTimes();
    if (frequency.getTrip() != null) {
      this.trip = new TripDTO(frequency.getTrip());
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
    Frequency frequency = new Frequency();
    frequency.setId(this.getId());
    frequency.setStartTime(this.getStartTime());
    frequency.setEndTime(this.getEndTime());
    frequency.setHeadwaySecs(this.getHeadwaySecs());
    frequency.setExactTimes(this.getExactTimes());
    if (this.getTrip() != null) {
      frequency.setTrip(this.getTrip().toTrip());
    }
    return frequency;
  }
}
/*% } %*/
