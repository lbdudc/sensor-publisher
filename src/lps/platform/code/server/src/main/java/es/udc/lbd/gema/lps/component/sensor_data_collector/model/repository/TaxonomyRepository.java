/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.Taxonomy;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.io.IOException;

public interface TaxonomyRepository {
  Taxonomy findByHasEmployee(Employee employee)
      throws JsonParseException, JsonMappingException, IOException;
}

/*% } %*/
