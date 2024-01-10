/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.Taxonomy;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyEntity;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomyNode;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class TaxonomyRepositoryImpl implements TaxonomyRepository {

  @PersistenceContext private EntityManager entityManager;

  public Taxonomy findByHasEmployee(Employee employee)
      throws JsonParseException, JsonMappingException, IOException {

    try {
      TaxonomyEntity taxonomyBd =
          (TaxonomyEntity)
              entityManager
                  .createQuery(
                      "SELECT t FROM TaxonomyEntity t "
                          + "inner join t.employees as e "
                          + "where :employeeId = e.id")
                  .setParameter("employeeId", employee.getId())
                  .getSingleResult();
      if (taxonomyBd == null) {
        return null;
      } else {

        ObjectMapper mapper = new ObjectMapper();
        Taxonomy taxonomy = new Taxonomy();
        // Building taxonomy from JSON and BD fetched object
        taxonomy.setEmployees(taxonomyBd.getEmployees());
        taxonomy.setName(taxonomyBd.getName());
        taxonomy.setId(taxonomyBd.getId());
        taxonomy.setRoot(mapper.readValue(taxonomyBd.getJsonTree(), TaxonomyNode.class));

        return taxonomy;
      }
    } catch (NoResultException e) {
      return null;
    }
  }
}

/*% } %*/
