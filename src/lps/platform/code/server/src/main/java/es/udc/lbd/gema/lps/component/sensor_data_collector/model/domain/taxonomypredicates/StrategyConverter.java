/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import javax.persistence.AttributeConverter;

public class StrategyConverter implements AttributeConverter<StrategyOperator, String> {

  @Override
  public String convertToDatabaseColumn(StrategyOperator attribute) {
    return attribute.getClass().getSimpleName().toLowerCase();
  }

  @Override
  public StrategyOperator convertToEntityAttribute(String dbData) {
    if (dbData.equals("countstrategy")) {
      return new CountStrategy();
    } else if (dbData.equals("gpsstrategy")) {
      return new GpsStrategy();
    }
    return null;
  }
}

/*% } %*/