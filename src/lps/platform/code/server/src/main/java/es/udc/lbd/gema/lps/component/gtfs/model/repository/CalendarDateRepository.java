/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.CalendarDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CalendarDateRepository
    extends JpaRepository<CalendarDate, String>, JpaSpecificationExecutor<CalendarDate> {

  CalendarDate save(CalendarDate calendarDate);

  Optional<CalendarDate> findById(Long pk);

  Page<CalendarDate> findByIdIn(List<Long> pk, Pageable pageable);
}
/*% } %*/
