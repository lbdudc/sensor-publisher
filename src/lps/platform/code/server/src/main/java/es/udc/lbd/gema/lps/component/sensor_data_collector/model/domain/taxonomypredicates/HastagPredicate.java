/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;

public class HastagPredicate extends Predicate {

  private String tag;

  public HastagPredicate() {}

  public HastagPredicate(String string) {
    this.tag = string;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public Boolean evaluate(TaxonomySegment s) {
    Boolean result = Boolean.FALSE;
    if (s.getTag() == null) return result;
    if (s.getTag().getName().equalsIgnoreCase(this.tag)) result = Boolean.TRUE;
    return result;
  }
}

/*% } %*/