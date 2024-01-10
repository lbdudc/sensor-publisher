/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.contextinformation;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "spatial_coll")
public class SpatialFeatureCollection {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spatialColGen")
  @SequenceGenerator(name = "spatialColGen", sequenceName = "spatial_col_id_seq", allocationSize = 1)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "collection")
  private Set<SpatialFeature> features = new HashSet<>();

  public SpatialFeatureCollection() {}

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

  public Set<SpatialFeature> getFeatures() {
    return features;
  }

  public void setFeatures(Set<SpatialFeature> features) {
    this.features = features;
  }
}

/*% } %*/
