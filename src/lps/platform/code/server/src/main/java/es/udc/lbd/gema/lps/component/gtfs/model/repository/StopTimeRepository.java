/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.StopTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StopTimeRepository
    extends JpaRepository<StopTime, Long>, JpaSpecificationExecutor<StopTime> {

  StopTime save(StopTime stopTime);

  Optional<StopTime> findById(Long pk);

  Page<StopTime> findByIdIn(List<Long> pk, Pageable pageable);
}
/*% } %*/
