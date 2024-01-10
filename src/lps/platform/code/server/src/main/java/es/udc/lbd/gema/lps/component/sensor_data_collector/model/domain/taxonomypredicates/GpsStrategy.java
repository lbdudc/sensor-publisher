/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import java.lang.reflect.Field;
import java.util.List;

public class GpsStrategy extends StrategyOperator {

  @Override
  public Boolean evaluate(List<Location> set, Object doubleValue, DataOperator op, String type) {
    Boolean result = Boolean.FALSE;
    Field f = null;
    Double value = (Double) doubleValue;
    try {
      f = Location.class.getField(type);
    } catch (NoSuchFieldException | SecurityException e) {
      e.printStackTrace();
    }
    for (Location loc : set) {
      try {
        Double locValue = (Double) f.get(loc);
        if (aux(locValue, value, op)) result = Boolean.TRUE;
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  private Boolean aux(Double locValue, Double value, DataOperator op) {
    switch (op) {
      case GREATER:
        return (locValue.doubleValue() > value.doubleValue());
      case GREATER_EQUAL:
        return (locValue.doubleValue() >= value.doubleValue());
      case EQUAL:
        return (locValue.doubleValue() == value.doubleValue());
      case LESS_EQUAL:
        return (locValue.doubleValue() <= value.doubleValue());
      case LESS:
        return (locValue.doubleValue() < value.doubleValue());
      case DISTINCT:
        return (locValue.doubleValue() != value.doubleValue());
      default:
        return null;
    }
  }
}

/*% } %*/