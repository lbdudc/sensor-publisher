/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Agency;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AgencyRepository
    extends JpaRepository<Agency, String>, JpaSpecificationExecutor<Agency> {

  Agency save(Agency agency);

  Optional<Agency> findById(String pk);

  Page<Agency> findByAgencyIdIn(List<String> pk, Pageable pageable);
}
/*% } %*/
