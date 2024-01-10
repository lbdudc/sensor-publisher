/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaxonomySegment {

  private Long id;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private List<Location> locations = new ArrayList<>();
  private Employee employee;
  private Tag tag;

  public TaxonomySegment() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public List<Location> getLocations() {
    return locations;
  }

  public void setLocations(List<Location> locations) {
    this.locations = locations;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public Tag getTag() {
    return tag;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  @Override
  public String toString() {
    return "TaxonomySegment [id="
        + id
        + ", startTime="
        + startTime
        + ", endTime="
        + endTime
        + ", locations="
        + locations
        + ", employee="
        + employee
        + ", tag="
        + tag
        + "]";
  }
}

/*% } %*/
