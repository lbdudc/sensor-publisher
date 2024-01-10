/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import java.util.List;

public class CountStrategy extends StrategyOperator {

  @Override
  public Boolean evaluate(List<Location> set, Object longValue, DataOperator op, String type) {
    Long locations = new Long(set.size());
    Long value = (Long) longValue;

    switch (op) {
      case GREATER:
        return (locations.longValue() > value.longValue());
      case GREATER_EQUAL:
        return (locations.longValue() >= value.longValue());
      case EQUAL:
        return (locations.longValue() == value.longValue());
      case LESS_EQUAL:
        return (locations.longValue() <= value.longValue());
      case LESS:
        return (locations.longValue() < value.longValue());
      case DISTINCT:
        return (locations.longValue() != value.longValue());
      default:
        return null;
    }
  }
}

/*% } %*/