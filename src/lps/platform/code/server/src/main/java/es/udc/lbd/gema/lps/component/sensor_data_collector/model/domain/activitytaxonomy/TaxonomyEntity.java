/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="ta_taxonomy_entity")
public class TaxonomyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taxonomyGen")
  @SequenceGenerator(name = "taxonomyGen", sequenceName = "taxonomy_id_seq", allocationSize = 1)
  private Long id;

  private String name;

  @Column(columnDefinition = "TEXT")
  private String jsonTree;

  @OneToMany private Set<Employee> employees = new HashSet<>();

  public TaxonomyEntity() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getJsonTree() {
    return jsonTree;
  }

  public void setJsonTree(String jsonTree) {
    this.jsonTree = jsonTree;
  }

  public Set<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(Set<Employee> employees) {
    this.employees = employees;
  }
}

/*% } %*/
