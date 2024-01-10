/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SegmentGroupDTO {
  List<SegmentDTO> segments;
  LocalDateTime initTime;
  LocalDateTime endTime;

  public SegmentGroupDTO() {}

  public List<SegmentDTO> getSegments() {
    return segments;
  }

  public void setSegments(List<SegmentDTO> segments) {
    this.segments = segments;
  }

  public LocalDateTime getInitTime() {
    return initTime;
  }

  public void setInitTime(LocalDateTime initTime) {
    this.initTime = initTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }
}

/*% } %*/
