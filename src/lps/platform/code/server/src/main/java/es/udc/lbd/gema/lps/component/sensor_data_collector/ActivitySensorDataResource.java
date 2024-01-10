/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector;

import es.udc.lbd.gema.lps.component.gema.model.service.ActivityService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ActivityDTO;
import es.udc.lbd.gema.lps.web.rest.util.HeaderUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(ActivitySensorDataResource.ACTIVITY_RESOURCE_URL)
public class ActivitySensorDataResource {

  public static final String ACTIVITY_RESOURCE_URL = "/api/sensor-data-collector/activities";

  private static final Logger log = LoggerFactory.getLogger(ActivitySensorDataResource.class);

  @Inject private ActivityService activityService;

  @GetMapping
  public ResponseEntity<List<ActivityDTO>> findAll(
    @RequestParam(name = "start", required = false) 
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime start,
    @RequestParam(name = "end", required = false) 
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime end,
    @RequestParam(name = "employee", required = false) List<Long> employee) {

    List<ActivityDTO> activitiesDTO;

    activitiesDTO =
        activityService.findAll(
            null,
            start.toLocalDate(),
            end.toLocalDate(),
            start.toLocalTime(),
            end.toLocalTime(),
            employee,
            null,
            null,
            null);

    return new ResponseEntity<>(activitiesDTO, HttpStatus.OK);
  }
}
/*% } %*/
