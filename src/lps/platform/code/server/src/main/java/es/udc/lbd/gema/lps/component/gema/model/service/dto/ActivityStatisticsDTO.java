/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import java.util.Map;

public class ActivityStatisticsDTO {

  private Map<String, String> values;

  public ActivityStatisticsDTO() {}

  public Map<String, String> getValues() {
    return values;
  }

  public void setValues(Map<String, String> values) {
    this.values = values;
  }
}
/*% } %*/
