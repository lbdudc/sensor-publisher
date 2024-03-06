/*% if (feature.MWM_TrajectoryAnnotation) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  Tag findByNameIgnoreCase(String name);
}

/*% } %*/
