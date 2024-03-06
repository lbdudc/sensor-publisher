/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import java.math.BigDecimal;

public class ActivityDistanceDurationDTO {

  private Double distance;

  private BigDecimal duration;

  public ActivityDistanceDurationDTO() {}

  public ActivityDistanceDurationDTO(Double distance, BigDecimal duration) {
    this.distance = distance;
    this.duration = duration;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public BigDecimal getDuration() {
    return duration;
  }

  public void setDuration(BigDecimal duration) {
    this.duration = duration;
  }
}
/*% } %*/
