/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;

public class GpsPredicate extends Predicate {

  private String type;

  private Double value;

  private DataOperator operator;

  public GpsPredicate() {}

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public DataOperator getOperator() {
    return operator;
  }

  public void setOperator(DataOperator operator) {
    this.operator = operator;
  }

  @Override
  public Boolean evaluate(TaxonomySegment s) {
    GpsStrategy strategy = new GpsStrategy();
    return strategy.evaluate(s.getLocations(), this.value, this.operator, this.type);
  }
}

/*% } %*/