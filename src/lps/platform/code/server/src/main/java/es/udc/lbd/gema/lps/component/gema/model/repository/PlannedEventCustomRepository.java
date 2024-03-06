/*% if (feature.MWM_TE_VisitsRecord) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import org.locationtech.jts.geom.Geometry;

public interface PlannedEventCustomRepository {
  Map<List<Long>, Geometry> findClusteredVisits(
      LocalDate startDate,
      LocalDate endDate,
      PlannedEventState state,
      List<Long> employees,
      List<Long> clients,
      Double distance);
}
/*% } %*/
