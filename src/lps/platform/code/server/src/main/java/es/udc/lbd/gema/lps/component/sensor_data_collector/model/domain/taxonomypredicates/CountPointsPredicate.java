/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;

public class CountPointsPredicate extends Predicate {

  private DataOperator operator;
  private Long value;

  public CountPointsPredicate() {}

  public Long getValue() {
    return value;
  }

  public void setValue(Long value) {
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
    CountStrategy strategy = new CountStrategy();
    return strategy.evaluate(s.getLocations(), this.value, this.operator);
  }
}

/*% } %*/