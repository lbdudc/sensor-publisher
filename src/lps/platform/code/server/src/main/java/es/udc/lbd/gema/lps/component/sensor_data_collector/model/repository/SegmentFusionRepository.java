/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.SegmentFusion;

public interface SegmentFusionRepository extends JpaRepository<SegmentFusion, Long> {

}
/*% } %*/