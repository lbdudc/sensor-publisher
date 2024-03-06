/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.predicate;

import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorPredicate;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorSpatialOperator;
import es.udc.lbd.gema.annotator.domain.taxonomypredicates.AnnotatorSpatialPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.Predicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.taxonomypredicates.SpatialPredicate;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.SpatialFeatureCollectionRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.AnnotatorLPSConversor;
import javax.inject.Inject;

public class SpatialPredicateHandler implements PredicateHandler {

  @Inject private SpatialFeatureCollectionRepository spatialFeatureCollectionRepository;
  @Inject private AnnotatorLPSConversor annotatorLPSConversor;

  @Override
  public AnnotatorPredicate handlePredicate(Predicate predicate) {
    SpatialPredicate spatialPredicate = (SpatialPredicate) predicate;
    AnnotatorSpatialPredicate annotatorSpatialPredicate = new AnnotatorSpatialPredicate();

    annotatorSpatialPredicate.setValue(spatialPredicate.getValue());
    annotatorSpatialPredicate.setOperator(
        AnnotatorSpatialOperator.valueOf(spatialPredicate.getOperator().name()));
    annotatorSpatialPredicate.setSpatialFeatureCollection(
        annotatorLPSConversor.lpsSpatialFeatureCollectionToAnnotatorSpatialFeatureCollection(
            spatialFeatureCollectionRepository.findByName(
                spatialPredicate.getSpatialFeatureCollection())));

    return annotatorSpatialPredicate;
  }
}

/*% } %*/