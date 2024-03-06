/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.contextinformation.SpatialFeatureCollection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpatialFeatureCollectionRepository
    extends JpaRepository<SpatialFeatureCollection, Long> {
  SpatialFeatureCollection findByName(String name);
}

/*% } %*/
