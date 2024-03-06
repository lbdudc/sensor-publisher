/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Trip;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TripRepository
    extends JpaRepository<Trip, String>, JpaSpecificationExecutor<Trip>, TripCustomRepository {

  Trip save(Trip trip);

  Optional<Trip> findById(String pk);

  Page<Trip> findByTripIdIn(List<String> pk, Pageable pageable);
}
/*% } %*/
