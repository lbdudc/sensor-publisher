/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;

public class ConstantPredicate extends Predicate {

  private Boolean value;

  public ConstantPredicate() {}

  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }

  @Override
  public Boolean evaluate(TaxonomySegment s) {
    return this.value;
  }
}

/*% } %*/