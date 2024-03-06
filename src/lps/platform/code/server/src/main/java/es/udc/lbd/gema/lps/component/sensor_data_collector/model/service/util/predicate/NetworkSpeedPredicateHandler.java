/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorNetworkSpeedPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.NetworkSpeedPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;

public class NetworkSpeedPredicateHandler implements PredicateHandler {

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    NetworkSpeedPredicate networkSpeedPredicate = (NetworkSpeedPredicate) predicate;

    return new AnnotatorNetworkSpeedPredicate(networkSpeedPredicate.getThreshold());
  }
}

/*% } %*/