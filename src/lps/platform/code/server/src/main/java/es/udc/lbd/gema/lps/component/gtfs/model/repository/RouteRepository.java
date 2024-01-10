/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.repository;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Route;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RouteRepository
    extends JpaRepository<Route, String>, JpaSpecificationExecutor<Route>, RouteCustomRepository {

  Route save(Route route);

  Optional<Route> findById(String pk);

  Page<Route> findByRouteIdIn(List<String> pk, Pageable pageable);
}
/*% } %*/
