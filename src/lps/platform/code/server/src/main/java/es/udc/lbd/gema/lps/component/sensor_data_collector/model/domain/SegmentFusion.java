/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ta_segment_fusion")
public class SegmentFusion {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "taxonomySegmentGen")
  @SequenceGenerator(name = "taxonomySegmentGen", sequenceName = "taxonomy_segment_id_seq", allocationSize = 1)
  private Long id;

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  @ManyToMany private List<Location> locations = new ArrayList<>();
  @ManyToOne private Employee employee;

  @ManyToOne(fetch = FetchType.EAGER)
  private Tag tag;

  public SegmentFusion() {}

  public SegmentFusion(TaxonomySegment taxonomySegment) {
    this.id = taxonomySegment.getId();
    this.startTime = taxonomySegment.getStartTime();
    this.endTime = taxonomySegment.getEndTime();
    this.locations = taxonomySegment.getLocations();
    this.employee = taxonomySegment.getEmployee();
    this.tag = taxonomySegment.getTag();
  }

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
