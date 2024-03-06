/*% if (feature.MWM_TE_Planning || feature.MWM_TE_ActivitiesRecord || feature.MWM_TE_Statistics) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTemporalRelation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.AdvancedQueryTimeComparation;
import es.udc.lbd.gema.lps.component.gema.model.domain.enums.DelimitedZoneSearchType;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometryDeserializer;
import es.udc.lbd.gema.lps.model.util.jackson.CustomGeometrySerializer;
import java.time.LocalDate;
import java.util.List;
import org.locationtech.jts.geom.Geometry;

public class ActivityRequestDTO {

  private List<Long> categories;
  private LocalDate start;
  private LocalDate end;
  private List<Long> employee;

  @JsonSerialize(using = CustomGeometrySerializer.class)
  @JsonDeserialize(using = CustomGeometryDeserializer.class)
  private Geometry bbox;

  private DelimitedZoneSearchType delimitedZoneSearchType;

  private List<Long> event;
  private List<Long> client;

  private AdvancedQueryTemporalRelation advancedQueryTemporalRelation;
  private Long categoryToSearch;
  private AdvancedQueryTimeComparation advancedQueryTimeComparation;
  private Long advancedQueryTime;

  public ActivityRequestDTO() {}

  public List<Long> getCategories() {
    return categories;
  }

  public void setCategories(List<Long> categories) {
    this.categories = categories;
  }

  public LocalDate getStart() {
    return start;
  }

  public void setStart(LocalDate start) {
    this.start = start;
  }

  public LocalDate getEnd() {
    return end;
  }

  public void setEnd(LocalDate end) {
    this.end = end;
  }

  public List<Long> getEmployee() {
    return employee;
  }

  public void setEmployee(List<Long> employee) {
    this.employee = employee;
  }

  public List<Long> getEvent() {
    return event;
  }

  public void setEvent(List<Long> event) {
    this.event = event;
  }

  public List<Long> getClient() {
    return client;
  }

  public void setClient(List<Long> client) {
    this.client = client;
  }

  public Geometry getBbox() {
    return bbox;
  }

  public void setBbox(Geometry bbox) {
    this.bbox = bbox;
  }

  public DelimitedZoneSearchType getDelimitedZoneSearchType() {
    return delimitedZoneSearchType;
  }

  public void setDelimitedZoneSearchType(DelimitedZoneSearchType delimitedZoneSearchType) {
    this.delimitedZoneSearchType = delimitedZoneSearchType;
  }

  public AdvancedQueryTemporalRelation getAdvancedQueryTemporalRelation() {
    return advancedQueryTemporalRelation;
  }

  public void setAdvancedQueryTemporalRelation(
    AdvancedQueryTemporalRelation advancedQueryTemporalRelation) {
    this.advancedQueryTemporalRelation = advancedQueryTemporalRelation;
  }

  public Long getCategoryToSearch() {
    return categoryToSearch;
  }

  public void setCategoryToSearch(Long categoryToSearch) {
    this.categoryToSearch = categoryToSearch;
  }

  public AdvancedQueryTimeComparation getAdvancedQueryTimeComparation() {
    return advancedQueryTimeComparation;
  }

  public void setAdvancedQueryTimeComparation(
    AdvancedQueryTimeComparation advancedQueryTimeComparation) {
    this.advancedQueryTimeComparation = advancedQueryTimeComparation;
  }

  public Long getAdvancedQueryTime() {
    return advancedQueryTime;
  }

  public void setAdvancedQueryTime(Long advancedQueryTime) {
    this.advancedQueryTime = advancedQueryTime;
  }
}
/*% } %*/
