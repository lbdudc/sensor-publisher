/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorCountPointsPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorDataOperator;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.CountPointsPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;

public class CountPointsPredicateHandler implements PredicateHandler {

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    CountPointsPredicate countPointsPredicate = (CountPointsPredicate) predicate;
    AnnotatorCountPointsPredicate annotatorCountPointsPredicate =
        new AnnotatorCountPointsPredicate();

    annotatorCountPointsPredicate.setValue(countPointsPredicate.getValue());
    annotatorCountPointsPredicate.setOperator(
        AnnotatorDataOperator.valueOf(countPointsPredicate.getOperator().name()));

    return annotatorCountPointsPredicate;
  }
}

/*% } %*/