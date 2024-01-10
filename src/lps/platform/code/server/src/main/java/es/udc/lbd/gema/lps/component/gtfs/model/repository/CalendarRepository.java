/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Calendar;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalendarRepository
    extends JpaRepository<Calendar, String>, JpaSpecificationExecutor<Calendar> {

  Calendar save(Calendar calendar);

  Optional<Calendar> findById(String pk);

  Page<Calendar> findByServiceIdIn(List<String> pk, Pageable pageable);
}
/*% } %*/
