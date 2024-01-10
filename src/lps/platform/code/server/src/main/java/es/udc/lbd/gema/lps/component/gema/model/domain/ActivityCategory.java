/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "te_activity_category")
public class ActivityCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activityCatGen")
  @SequenceGenerator(name = "activityCatGen", sequenceName = "te_activity_category_id_seq", allocationSize = 1)
  private Long id;

  private String label;

  private String color;

  public ActivityCategory() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Override
  public String toString() {
    return "ActivityCategory [id=" + id + ", label=" + label + ", color=" + color + "]";
  }
}
/*% } %*/
