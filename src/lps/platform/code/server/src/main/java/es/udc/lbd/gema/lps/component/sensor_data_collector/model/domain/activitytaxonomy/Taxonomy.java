/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.util.HashSet;
import java.util.Set;

public class Taxonomy {

  private Long id;

  private String name;

  private TaxonomyNode root;

  private Set<Employee> employees = new HashSet<>();

  public Taxonomy() {}

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

  public TaxonomyNode getRoot() {
    return root;
  }

  public void setRoot(TaxonomyNode root) {
    this.root = root;
  }

  public Set<Employee> getEmployees() {
    return employees;
  }

  public void setEmployees(Set<Employee> employees) {
    this.employees = employees;
  }

  public TaxonomyNode importJson(String json) {
    return null;
  }

  public void export(TaxonomyNode root) {}
}

/*% } %*/
