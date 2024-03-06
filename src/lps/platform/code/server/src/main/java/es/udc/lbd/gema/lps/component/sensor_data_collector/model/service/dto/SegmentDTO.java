/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Segment;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import java.time.LocalDateTime;

public class SegmentDTO {
  private Long id;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private LocalDateTime creationDate;
  private TagDTO tag;
  private EmployeeDTO employee;

  public SegmentDTO() {}

  public SegmentDTO(Segment segment) {
    this.id = segment.getId();
    this.startTime = segment.getStartTime();
    this.endTime = segment.getEndTime();
    this.tag = new TagDTO(segment.getTag());
    this.employee = new EmployeeDTO(segment.getEmployee());
    this.creationDate = segment.getCreationDate();
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

  public TagDTO getTag() {
    return tag;
  }

  public void setTag(TagDTO tag) {
    this.tag = tag;
  }

  public EmployeeDTO getEmployee() {
    return employee;
  }

  public void setEmployee(EmployeeDTO employee) {
    this.employee = employee;
  }

  public LocalDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }
}

/*% } %*/
