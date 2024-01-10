/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorLogicPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorlogicOperator;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.LogicPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.AnnotatorLPSConversor;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.exception.ConversionException;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class LogicPredicateHandler implements PredicateHandler {

  @Inject private AnnotatorLPSConversor annotatorLPSConversor;

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    LogicPredicate logicPredicate = (LogicPredicate) predicate;
    AnnotatorLogicPredicate annotatorLogicPredicate = new AnnotatorLogicPredicate();

    annotatorLogicPredicate.setOp(AnnotatorlogicOperator.valueOf(logicPredicate.getOp().name()));
    annotatorLogicPredicate.setChildren(
        logicPredicate.getChildren().stream()
            .map(
                p -> {
                  try {
                    return annotatorLPSConversor.lpsPredicateToAnnotatorPredicate(p);
                  } catch (ConversionException e) {
                    return null;
                  }
                })
            .collect(Collectors.toSet()));

    return annotatorLogicPredicate;
  }
}

/*% } %*/