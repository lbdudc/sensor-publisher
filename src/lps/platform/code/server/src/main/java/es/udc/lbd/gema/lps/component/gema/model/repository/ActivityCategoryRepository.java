/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.repository;

import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import java.util.List;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Long> {
  /*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
  ActivityCategory findByLabel(String label);
  /*% } %*/
}
/*% } %*/
