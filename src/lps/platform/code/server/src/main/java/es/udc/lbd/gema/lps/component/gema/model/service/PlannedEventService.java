/*% if (feature.MWM_M_PlannedVisit) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service;

/*% if (feature.MWM_M_Client) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.Client;
/*% } %*/
/*% if (feature.MWM_M_Employee) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
/*% } %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEvent;
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.component.gema.model.repository.PlannedEventRepository;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.PlannedEventDTO;
import es.udc.lbd.gema.lps.config.Properties;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.spatial_specifications.*;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import javax.inject.Inject;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*% if (feature.MWM_VisitSchedule) { %*/
import es.udc.lbd.gema.lps.component.gema.model.repository.ClientRepository;
import es.udc.lbd.gema.lps.component.gema.model.repository.EmployeeRepository;
import es.udc.lbd.gema.lps.config.Properties;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
/*% } %*/
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import org.springframework.security.access.prepost.PreAuthorize;
import es.udc.lbd.gema.lps.model.domain.user_management.Authority;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.service.UMUserService;
import es.udc.lbd.gema.lps.security.SecurityUtils;
import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
/*% } %*/
/*% if (feature.MWM_TE_VisitsRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClusteredPlannedEventsDTO;
/*% } %*/
/*% if (feature.MWM_TE_T_ClientCustomLink || feature.MWM_VisitSchedule) { %*/
import org.springframework.beans.factory.annotation.Value;
/*% } %*/

@Service
@Transactional
public class PlannedEventService {

  /*% if (feature.MWM_TE_T_ClientCustomLink) { %*/
  @Value("${trajectory-exploitation.detailUrl:${properties.clientHost}/staff/clients/{id}}")
  private String clientDetailUrl;
  /*% } %*/

  /*% if (feature.MWM_TE_Realtime || feature.MWM_TE_Planning || feature.MWM_VisitSchedule) { %*/
  @Inject private Properties properties;
  /*% } %*/

  @Inject
  private PlannedEventRepository plannedEventRepository;

  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @Inject private UMUserService umUserService;
   /*% } %*/
  /*% if (feature.MWM_VisitSchedule) { %*/
  @Inject private ClientRepository clientRepository;
  @Inject private EmployeeRepository employeeRepository;

  private static final String MONTH_VIEW_STRING = "month";
  private static final String WEEK_VIEW_STRING = "week";
  private static final String DAY_VIEW_STRING = "day";

  @Value("${event-calendar.event-day-size-threshold}")
  private Long dayThreshold;

  @Value("${event-calendar.event-week-size-threshold}")
  private Long weekThreshold;

  public Page<PlannedEventDTO> getAllForCalendar(
    Pageable pageable,
    List<Long> employees,
    Long start,
    Long end,
    String view) {

    Page<PlannedEvent> page;

    LocalDate startDate =
      start != null
        ? Instant.ofEpochMilli(start).atZone(ZoneId.systemDefault()).toLocalDate()
        : null;
    LocalDate endDate =
      end != null ? Instant.ofEpochMilli(end).atZone(ZoneId.systemDefault()).toLocalDate() : null;

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      employees = Arrays.asList(umUser.getEmployee().getId());
    }

    /*% } %*/
    if (view != null && view.equalsIgnoreCase(MONTH_VIEW_STRING)) {

      // Number of events per day that exceed dayThreshold
      Map<LocalDate, Long> eventsPerDay =
        plannedEventRepository
          .countByEmployeeIdInAndDateBetween(employees, startDate, endDate, dayThreshold)
          .stream()
          .collect(
            Collectors.toMap(
              tuple -> ((LocalDate) tuple.get("date")),
              tuple -> ((Long) tuple.get("count"))));

      List<PlannedEvent> events = new ArrayList<PlannedEvent>();

      // Creating group of events for day with too much events
      for (Map.Entry<LocalDate, Long> entry : eventsPerDay.entrySet()) {
        PlannedEvent event = new PlannedEvent();
        event.setDescription(entry.getValue().toString());
        event.setDate(entry.getKey());
        events.add(event);
      }

      // Fetching events for days with low number of them
      Set<LocalDate> invalidDates = eventsPerDay.keySet().size() > 0 ? eventsPerDay.keySet() : null;

      events.addAll(
        plannedEventRepository.findByEmployeeIdInAndDateNotIn(
          employees, invalidDates, startDate, endDate));

      // Creating page from list
      page = new PageImpl<PlannedEvent>(events, pageable, events.size());

    } else {
      page =
        plannedEventRepository.findByEmployeeIdInAndDateBetween(
          employees,
          startDate,
          endDate,
          pageable);
      if (view != null && employees == null) {
        if (view.equalsIgnoreCase(DAY_VIEW_STRING)
            && page.getNumberOfElements() > dayThreshold) {
          return null;
        }
        if (view.equalsIgnoreCase(WEEK_VIEW_STRING)
            && page.getNumberOfElements() > weekThreshold) {
          return null;
        }
      }
    }

    return page.map(plannedEvent -> new PlannedEventDTO(plannedEvent));

  }

  public Page<PlannedEventDTO> findAllPageable(
    Pageable pageable, Specification specification, List<String> labels, Geometry bbox) {

    Page<PlannedEvent> page;

    // Fixing SRID
    if (bbox != null) {
      bbox.setSRID(properties.getGis().getDefaultSrid());
    }

    // We have to add this spec manually because we want partial matches
    GenericSpecification<PlannedEvent> labelsSpecification = null;
    if (labels != null && labels.size() > 0) {
      labelsSpecification = new GenericSpecification();
      for (String label : labels) {
        labelsSpecification.add(
          new SearchCriteria("label", label, SearchOperation.MATCH, LogicalOperation.AND));
      }
    }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    GenericSpecification<PlannedEvent> employeeSpecification = null;
    UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      employeeSpecification = new GenericSpecification();
      employeeSpecification.add(
        new SearchCriteria("employee", umUser.getEmployee().getId(), SearchOperation.EQUAL, LogicalOperation.AND));
    }
    /*% } %*/

    Specification bboxSpec = new WithinSpecification(bbox, "geom");

    page =
      plannedEventRepository.findAll(
        bboxSpec.and(specification).and(labelsSpecification)/*% if (feature.MWM_EmployeeAuthentication) { %*/.and(employeeSpecification)/*% } %*/,
        pageable);

    return page.map(plannedEvent -> new PlannedEventDTO(plannedEvent));
  }
  /*% } %*/
  /*% if (feature.MWM_TE_Realtime || feature.MWM_TE_Planning || feature.MWM_VisitSchedule) { %*/

  public List<PlannedEventDTO> findAll(
    Specification specification, List<String> labels, Geometry bbox) {

    // Fixing SRID
    if (bbox != null) {
      bbox.setSRID(properties.getGis().getDefaultSrid());
    }

    // We have to add the labels spec manually because we want partial matches
    GenericSpecification<PlannedEvent> labelsSpecification = null;
    if (labels != null && labels.size() > 0) {
      labelsSpecification = new GenericSpecification();
      for (String label : labels) {
        labelsSpecification.add(
          new SearchCriteria("label", label, SearchOperation.MATCH, LogicalOperation.AND));
      }
    }

    Specification bboxSpec = new WithinSpecification(bbox, "geom");

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    GenericSpecification<PlannedEvent> employeeSpecification = null;
    UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      employeeSpecification = new GenericSpecification();
      employeeSpecification.add(
        new SearchCriteria("employee", umUser.getEmployee().getId(), SearchOperation.EQUAL, LogicalOperation.AND));
    }
    /*% } %*/

    List<PlannedEvent> result =
      plannedEventRepository.findAll(
        bboxSpec.and(specification).and(labelsSpecification)/*% if (feature.MWM_EmployeeAuthentication) { %*/.and(employeeSpecification)/*% } %*/,
        Sort.by("date").ascending().and(Sort.by(/*% if (feature.MVM_VT_OnlyDay) { %*/"eventOrder"/*% } else { %*/"startTime"/*% } %*/)));

    return result.stream()
      .map(plannedEvent -> new PlannedEventDTO(plannedEvent))
      .collect(Collectors.toList());
  }
  /*% } %*/
  /*% if (feature.MWM_TE_VisitsRecord) { %*/
  public Page<PlannedEventDTO> findAllById(Pageable pageable, Specification idsSpec) {

    Page<PlannedEvent> page = plannedEventRepository.findAll(idsSpec, pageable);
    return page.map(plannedEvent -> new PlannedEventDTO(plannedEvent));
  }

  public List<ClusteredPlannedEventsDTO> findClusteredVisits(
      LocalDate startDate,
      LocalDate endDate,
      PlannedEventState state,
      List<Long> employeesId,
      List<Long> clientsId,
      Double distance) {
    List<ClusteredPlannedEventsDTO> clusterDTOList = new ArrayList<>();
    Map<List<Long>, Geometry> clusters =
        plannedEventRepository.findClusteredVisits(
            startDate, endDate, state, employeesId, clientsId, distance);
    for (Map.Entry<List<Long>, Geometry> cluster : clusters.entrySet()) {
      ClusteredPlannedEventsDTO clusterDTO =
          new ClusteredPlannedEventsDTO(cluster.getKey(), cluster.getValue());
      clusterDTOList.add(clusterDTO);
    }

    return clusterDTOList;
  }
  /*% } %*/
  /*% if (feature.MWM_TE_Realtime || feature.MWM_TE_Planning || feature.MWM_VisitSchedule || feature.MWM_TE_ActivitiesRecord) { %*/
  public PlannedEventDTO findById(Long id) /*% if (feature.MWM_EmployeeAuthentication) { %*/ throws ForbiddenUserException /*% } %*/ {
    PlannedEvent plannedEvent = plannedEventRepository.findById(id).orElse(null);
    if (plannedEvent != null) {
      /*% if (feature.MWM_EmployeeAuthentication) { %*/
      UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
      if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
    	  if (plannedEvent.getEmployee().getId() != umUser.getEmployee().getId()) {
    	    throw new ForbiddenUserException();
    	  }
      }
      /*% } %*/
      return new PlannedEventDTO(plannedEvent);
    } else {
      return null;
    }
  }
  /*% } %*/
  /*% if (feature.MWM_VisitSchedule) { %*/
  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  /*% } %*/
  public PlannedEventDTO createPlannedEvent(PlannedEventDTO plannedEventDTO) {
    PlannedEvent plannedEvent = new PlannedEvent();

    plannedEvent.setDescription(plannedEventDTO.getDescription());
    plannedEvent.setState(plannedEventDTO.getState());
    plannedEvent.setDate(plannedEventDTO.getDate());
    /*% if (feature.MVM_VT_WithTime) { %*/
    plannedEvent.setStartTime(plannedEventDTO.getStartTime());
    plannedEvent.setEndTime(plannedEventDTO.getEndTime());
    /*% } %*/
    plannedEvent.setGeom(plannedEventDTO.getGeom());
    plannedEvent.setAddress(plannedEventDTO.getAddress());

    Optional<Client> optionalClient = clientRepository.findById(plannedEventDTO.getClient().getId());
    if (optionalClient.isPresent()) {
      Client clientBD = optionalClient.get();
      plannedEvent.setClient(clientBD);
    }

    Optional<Employee> optionalEmployee = employeeRepository.findById(plannedEventDTO.getEmployee().getId());
    if (optionalEmployee.isPresent()) {
      Employee employeeBD = optionalEmployee.get();
      plannedEvent.setEmployee(employeeBD);
    }
    /*% if (feature.MVM_VT_OnlyDay) { %*/

    plannedEvent.setEventOrder(getCountEvents(plannedEvent.getDate(), plannedEvent.getEmployee()) + 1);
    /*% } %*/

    PlannedEvent plannedEventBd = plannedEventRepository.save(plannedEvent);

    return new PlannedEventDTO(plannedEventBd);
  }

  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  /*% } %*/
  public PlannedEventDTO updatePlannedEvent(PlannedEventDTO plannedEventDTO) {
    PlannedEvent plannedEvent = plannedEventRepository.findById(plannedEventDTO.getId()).orElse(null);

    if (plannedEvent == null) {
      return null;
    }

    plannedEvent.setDescription(plannedEventDTO.getDescription());
    plannedEvent.setState(plannedEventDTO.getState());
    plannedEvent.setDate(plannedEventDTO.getDate());
    /*% if (feature.MVM_VT_WithTime) { %*/
    plannedEvent.setStartTime(plannedEventDTO.getStartTime());
    plannedEvent.setEndTime(plannedEventDTO.getEndTime());
    /*% } else { %*/
    plannedEvent.setEventOrder(plannedEventDTO.getEventOrder());
    /*% } %*/
    plannedEvent.setGeom(plannedEventDTO.getGeom());
    plannedEvent.setAddress(plannedEventDTO.getAddress());

    Optional<Client> optionalClient = clientRepository.findById(plannedEventDTO.getClient().getId());
    if (optionalClient.isPresent()) {
      Client clientBD = optionalClient.get();
      plannedEvent.setClient(clientBD);
    }

    Optional<Employee> optionalEmployee = employeeRepository.findById(plannedEventDTO.getEmployee().getId());
    if (optionalEmployee.isPresent()) {
      Employee employeeBD = optionalEmployee.get();
      plannedEvent.setEmployee(employeeBD);
    }

    PlannedEvent plannedEventBd = plannedEventRepository.save(plannedEvent);
    return new PlannedEventDTO(plannedEventBd);
  }

  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  /*% } %*/
  public Long delete(Long id) {
    PlannedEvent plannedEvent = plannedEventRepository.findById(id).orElse(null);
    if (plannedEvent != null) {
      plannedEventRepository.delete(plannedEvent);
      //Reordering the events of the day
      /*% if (feature.MVM_VT_OnlyDay) { %*/
      reOrder(plannedEvent.getDate(), plannedEvent.getEmployee(), plannedEvent.getEventOrder());
      /*% } %*/
      return id;
    } else {
      return null;
    }
  }

  /*% if (feature.MVM_VT_OnlyDay) { %*/
  /**
   * Get the count of the events of a day
   */
  private Integer getCountEvents(LocalDate date, Employee employee) {
    List<PlannedEvent> updateList =
      plannedEventRepository.findByDateAndEmployeeOrderByDate(date, employee);
    return updateList.size();
  }

  /**
   * Reorder the events of a day
   */
  private void reOrder(LocalDate date, Employee employee, Integer order) {
    // Update the events of the employee of the day whose order is higher than that of the inserted event
    List<PlannedEvent> updateList =
      plannedEventRepository.findByDateAndEmployeeAndEventOrderGreaterThan(date, employee, order);
    for (PlannedEvent p : updateList) {
      p.setEventOrder(p.getEventOrder() - 1);
      plannedEventRepository.save(p);
    }
  }
  /*% } %*/

  /*% } %*/
}
/*% } %*/
