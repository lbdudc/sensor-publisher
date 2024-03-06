/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorHastagPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.HastagPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;

public class HastagPredicateHandler implements PredicateHandler {

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    HastagPredicate hastagPredicate = (HastagPredicate) predicate;
    AnnotatorHastagPredicate annotatorHastagPredicate = new AnnotatorHastagPredicate();

    annotatorHastagPredicate.setTag(hastagPredicate.getTag());

    return annotatorHastagPredicate;
  }
}

/*% } %*/