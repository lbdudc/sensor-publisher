/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import java.util.List;

public abstract class StrategyOperator {

  public Boolean evaluate(List<Location> set, Object value, DataOperator op) {
    return evaluate(set, value, op, null);
  }

  public abstract Boolean evaluate(List<Location> set, Object value, DataOperator op, String type);
}

/*% } %*/