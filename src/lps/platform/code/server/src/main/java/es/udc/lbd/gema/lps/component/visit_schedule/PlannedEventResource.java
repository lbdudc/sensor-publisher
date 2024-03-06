/*% if (feature.MWM_VisitSchedule) { %*/
package es.udc.lbd.gema.lps.component.visit_schedule;

import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.component.gema.model.service.PlannedEventService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.PlannedEventDTO;
import es.udc.lbd.gema.lps.web.rest.util.HeaderUtil;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.inject.Inject;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
/*% } %*/

@RestController (value = "PEEPlannedEventResource")
@RequestMapping(PlannedEventResource.PLANNED_EVENT_RESOURCE_URL)
public class PlannedEventResource {

  public static final String PLANNED_EVENT_RESOURCE_URL = "/api/planned_event_crud/events";

  private static final Logger log = LoggerFactory.getLogger(PlannedEventResource.class);

  @Inject private PlannedEventService plannedEventService;

  @GetMapping
  public ResponseEntity<Page<PlannedEventDTO>> findAll(
    @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
    @And({
      @Spec(
        path = "date",
        params = "start",
        spec = GreaterThanOrEqual.class),
      @Spec(
        path = "date",
        params = "end",
        spec = LessThanOrEqual.class),
      @Spec(path = "employee.id", paramSeparator = ',', params = "employee", spec = In.class),
      @Spec(path = "client.id", paramSeparator = ',', params = "client", spec = In.class),
      @Spec(path = "description", spec = Like.class),
      @Spec(path = "state", spec = Equal.class)
    })
      Specification<PlannedEvent> specification,
    @RequestParam(name = "label", required = false) List<String> label,
    @RequestParam(name = "bbox", required = false) String wkt) {

    Page<PlannedEventDTO> page;
    Geometry bbox = null;

    if (wkt != null) {
      WKTReader reader = new WKTReader();
      try {
        bbox = reader.read(wkt);
      } catch (ParseException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }

    page = plannedEventService.findAllPageable(pageable, specification, label, bbox);

    HttpHeaders headers =
      PaginationUtil.generatePaginationHttpHeaders(page, PLANNED_EVENT_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/calendar")
  public ResponseEntity<Page<PlannedEventDTO>> findAllForCalendar(
      @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
      @RequestParam(value = "employees", required = false) List<Long> employees,
      @RequestParam(name = "start", required = false) Long start,
      @RequestParam(name = "end", required = false) Long end,
      @RequestParam(name = "view", required = false) String view) {

    Page<PlannedEventDTO> page =
        plannedEventService.getAllForCalendar(
          pageable, employees, start, end, view);

    if (page == null) {
      return ResponseEntity.badRequest()
        .headers(HeaderUtil.createError("event_calendar.employee.mandatory", null))
        .body(null);
    }

    HttpHeaders headers =
        PaginationUtil.generatePaginationHttpHeaders(page, PLANNED_EVENT_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PlannedEventDTO> findById(@PathVariable Long id) {
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    PlannedEventDTO plannedEventDTO;
    try {
      plannedEventDTO = plannedEventService.findById(id);
    } catch (ForbiddenUserException e) {
		  return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    /*% } else { %*/
    PlannedEventDTO plannedEventDTO = plannedEventService.findById(id);
    /*% } %*/
    if (plannedEventDTO != null) {
      return new ResponseEntity<>(plannedEventDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping
  public ResponseEntity<PlannedEventDTO> createPlannedEvent(@RequestBody PlannedEventDTO eventDTO) {
    PlannedEventDTO plannedEventCreatedDTO = plannedEventService.createPlannedEvent(eventDTO);
    return new ResponseEntity<>(plannedEventCreatedDTO, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PlannedEventDTO> updatePlannedEvent(
      @RequestBody PlannedEventDTO plannedEventDTO, @PathVariable Long id) {
    if (!plannedEventDTO.getId().equals(id)) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    PlannedEventDTO plannedEventUpdated = plannedEventService.updatePlannedEvent(plannedEventDTO);
    if (plannedEventUpdated != null) {
      return new ResponseEntity<>(plannedEventUpdated, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    Long idDeleted = plannedEventService.delete(id);
    if (idDeleted != null) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

}
/*% } %*/
