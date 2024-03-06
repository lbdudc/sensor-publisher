/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorConstantPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.ConstantPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;

public class ConstantPredicateHandler implements PredicateHandler {

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    ConstantPredicate constantPredicate = (ConstantPredicate) predicate;
    AnnotatorConstantPredicate annotatorConstantPredicate = new AnnotatorConstantPredicate();

    annotatorConstantPredicate.setValue(constantPredicate.getValue());

    return annotatorConstantPredicate;
  }
}

/*% } %*/