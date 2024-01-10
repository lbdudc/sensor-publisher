/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.LocationService;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.LocationDTO;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.LocationGroupDTO;
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
@RequestMapping(LocationResource.LOCATION_RESOURCE_URL)
public class LocationResource {

  public static final String LOCATION_RESOURCE_URL =
      "/api/sensor-data-collector/employees/{id}/location";

  private static final Logger log = LoggerFactory.getLogger(LocationResource.class);

  @Inject private LocationService locationService;

  @PostMapping
  public ResponseEntity pushLocation(@RequestBody String data, @PathVariable Long id) {
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
      List<LocationDTO> locationDtoList =
          mapper.readValue(data, new TypeReference<List<LocationDTO>>() {});
      return pushLocationList(locationDtoList, id);
    } catch (IOException e1) {
      try {
        LocationDTO locationDto = mapper.readValue(data, LocationDTO.class);
        return pushSingleLocation(locationDto, id);
      } catch (IOException e2) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }
  }

  public ResponseEntity<List<LocationDTO>> pushLocationList(
      List<LocationDTO> locationDtoList, Long id) {
    for (LocationDTO locationDto : locationDtoList) {
      if (locationDto.getEmployee() == null) {
        EmployeeDTO employeeDto = new EmployeeDTO();
        employeeDto.setId(id);
        locationDto.setEmployee(employeeDto);
      } else if (!locationDto.getEmployee().getId().equals(id)) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }
    try {
      List<LocationDTO> bdLocationDtoList = locationService.createLocation(locationDtoList);
      return new ResponseEntity<List<LocationDTO>>(bdLocationDtoList, HttpStatus.OK);
    } catch (NoSuchElementException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  public ResponseEntity<LocationDTO> pushSingleLocation(LocationDTO locationDto, Long id) {
    if (locationDto.getEmployee() == null) {
      EmployeeDTO employeeDto = new EmployeeDTO();
      employeeDto.setId(id);
      locationDto.setEmployee(employeeDto);
    } else if (!locationDto.getEmployee().getId().equals(id)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    try {
      LocationDTO bdLocationDto = locationService.createLocation(locationDto);
      return new ResponseEntity<LocationDTO>(bdLocationDto, HttpStatus.OK);
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
        List<LocationGroupDTO> locationsGroups =
            locationService.findAllByGroupsOf(start, end, id, interval);
        return new ResponseEntity<List<LocationGroupDTO>>(locationsGroups, HttpStatus.OK);
      } else {
        List<LocationDTO> locationListDto = locationService.findAll(start, end, id);
        return new ResponseEntity<List<LocationDTO>>(locationListDto, HttpStatus.OK);
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
