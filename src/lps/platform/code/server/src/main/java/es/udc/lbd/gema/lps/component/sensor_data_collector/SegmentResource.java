/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.SegmentService;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.SegmentDTO;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.SegmentGroupDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;

/*% } %*/
@RestController
@RequestMapping(SegmentResource.SEGMENT_RESOURCE_URL)
public class SegmentResource {

  public static final String SEGMENT_RESOURCE_URL =
      "/api/sensor-data-collector/employees/{id}/segment";

  private static final Logger log = LoggerFactory.getLogger(SegmentResource.class);

  @Inject private SegmentService segmentService;

  @PostMapping
  public ResponseEntity pushSegment(@RequestBody String data, @PathVariable Long id) {
    if (id == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    ObjectMapper mapper = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    // TimeStamp deserializer
    javaTimeModule.addDeserializer(
        LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.SSSSSS]")));
    mapper.registerModule(javaTimeModule);
    try {
      List<SegmentDTO> segmentDtoList =
          mapper.readValue(data, new TypeReference<List<SegmentDTO>>() {});
      return pushSegmentList(segmentDtoList, id);
    } catch (IOException e1) {
      try {
        SegmentDTO segmentDto = mapper.readValue(data, SegmentDTO.class);
        return pushSingleSegment(segmentDto, id);
      } catch (IOException e2) {
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
      }
    }
  }

  public ResponseEntity<List<SegmentDTO>> pushSegmentList(
      List<SegmentDTO> segmentDtoList, Long id) {
    for (SegmentDTO segmentDto : segmentDtoList) {
      if (segmentDto.getEmployee() == null) {
        EmployeeDTO employeeDto = new EmployeeDTO();
        employeeDto.setId(id);
        segmentDto.setEmployee(employeeDto);
      } else if (!segmentDto.getEmployee().getId().equals(id)) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }
    try {
      List<SegmentDTO> bdSegmentDtoList = segmentService.createSegment(segmentDtoList);
      return new ResponseEntity<List<SegmentDTO>>(bdSegmentDtoList, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  public ResponseEntity<SegmentDTO> pushSingleSegment(SegmentDTO segmentDto, Long id) {
    if (segmentDto.getEmployee() == null) {
      EmployeeDTO employeeDto = new EmployeeDTO();
      employeeDto.setId(id);
      segmentDto.setEmployee(employeeDto);
    } else if (!segmentDto.getEmployee().getId().equals(id)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
      SegmentDTO bdSegmentDto = segmentService.createSegment(segmentDto);
      return new ResponseEntity<SegmentDTO>(bdSegmentDto, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping
  public ResponseEntity findAll(
      @RequestParam(name = "start", required = true) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime start,
      @RequestParam(name = "end", required = true) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime end,
      @RequestParam(name = "groupsOf", required = false) Integer interval,
      @PathVariable Long id) {
    try {
      if (interval != null) {
        List<SegmentGroupDTO> segmentGroupListDto =
            segmentService.findAllByGroupsOf(start, end, id, interval);
        return new ResponseEntity<List<SegmentGroupDTO>>(segmentGroupListDto, HttpStatus.OK);
      } else {
        List<SegmentDTO> segmentListDto = segmentService.findAll(start, end, id);
        return new ResponseEntity<List<SegmentDTO>>(segmentListDto, HttpStatus.OK);
      }
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } /*% if (feature.MWM_EmployeeAuthentication) { %*/ catch (ForbiddenUserException e) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    /*% } %*/
  }
}

/*% } %*/
