/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Frequency;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FrequencyRepository
    extends JpaRepository<Frequency, Long>, JpaSpecificationExecutor<Frequency> {

  Frequency save(Frequency frequency);

  Optional<Frequency> findById(Long pk);

  Page<Frequency> findByIdIn(List<Long> pk, Pageable pageable);
}
/*% } %*/
