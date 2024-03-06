/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import org.springframework.beans.factory.annotation.Autowired;

public class NetworkSpeedPredicate extends Predicate {

  private Double threshold;

  public NetworkSpeedPredicate(Double threshold) {
    this.threshold = threshold;
  }

  public Double getThreshold() {
    return threshold;
  }

  public void setThreshold(Double threshold) {
    this.threshold = threshold;
  }

  @Override
  public Boolean evaluate(TaxonomySegment s) {
    Boolean result = Boolean.FALSE;
    // TODO check segment locations speed > kmh (fetched from DAO) * threshold
    return result;
  }
}

/*% } %*/