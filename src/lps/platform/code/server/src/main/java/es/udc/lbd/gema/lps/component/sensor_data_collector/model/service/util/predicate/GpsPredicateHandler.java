/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorDataOperator;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorGpsPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.GpsPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;

public class GpsPredicateHandler implements PredicateHandler {

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    GpsPredicate gpsPredicate = (GpsPredicate) predicate;
    AnnotatorGpsPredicate annotatorPredicate = new AnnotatorGpsPredicate();

    annotatorPredicate.setType(gpsPredicate.getType());
    annotatorPredicate.setValue(gpsPredicate.getValue());
    annotatorPredicate.setOperator(
        AnnotatorDataOperator.valueOf(gpsPredicate.getOperator().name()));

    return annotatorPredicate;
  }
}

/*% } %*/