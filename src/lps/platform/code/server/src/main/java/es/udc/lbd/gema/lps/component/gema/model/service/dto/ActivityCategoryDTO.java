/*% if (feature.MWM_M_Activity) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;

public class ActivityCategoryDTO {

  private Long id;
  private String label;
  private String color;

  public ActivityCategoryDTO() {}

  public ActivityCategoryDTO(ActivityCategory activityCategory) {
    this.id = activityCategory.getId();
    this.label = activityCategory.getLabel();
    this.color = activityCategory.getColor();
  }

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
}
/*% } %*/
