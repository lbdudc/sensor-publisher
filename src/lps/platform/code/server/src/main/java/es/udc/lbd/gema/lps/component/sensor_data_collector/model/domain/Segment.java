/*% if (feature.MWM_TrajectoryAnnotation) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ta_segment")
public class Segment {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "segmentGen")
  @SequenceGenerator(name = "segmentGen", sequenceName = "ta_segment_id_seq", allocationSize = 1)
  private Long id;

  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private LocalDateTime creationDate;

  @ManyToOne(optional = false)
  private Tag tag;

  @ManyToOne(optional = false)
  private Employee employee;

  public Segment() {}

  public Segment(
      Long id,
      LocalDateTime startTime,
      LocalDateTime endTime,
      LocalDateTime creationDate,
      Tag tag,
      Employee employee) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
    this.tag = tag;
    this.employee = employee;
    this.creationDate = creationDate;
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

  public Tag getTag() {
    return tag;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public String toString() {
    return "Segment [id="
        + id
        + ", startTime="
        + startTime
        + ", endTime="
        + endTime
        + ", creationDate="
        + creationDate
        + ", tag="
        + tag
        + ", employee="
        + employee
        + "]";
  }
}

/*% } %*/
