/*% if (feature.MWM_TA_TrajectoryAnnotator) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.lbd.gema.annotator.domain.AnnotatorActivityCategory;
import es.udc.lbd.gema.annotator.domain.AnnotatorEmployee;
import es.udc.lbd.gema.annotator.domain.AnnotatorSegment;
import es.udc.lbd.gema.annotator.domain.activitytaxonomy.AnnotatorTaxonomyNode;
import es.udc.lbd.gema.annotator.service.AnnotatorService;
import es.udc.lbd.gema.lps.component.gema.model.domain.Activity;
import es.udc.lbd.gema.lps.component.gema.model.domain.ActivityCategory;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.repository.ActivityCategoryRepository;
import es.udc.lbd.gema.lps.component.gema.model.repository.ActivityRepository;
import es.udc.lbd.gema.lps.component.gema.model.repository.EmployeeRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Segment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.Taxonomy;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.activitytaxonomy.TaxonomySegment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.LocationRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.SegmentRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.TaxonomyRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.AnnotatorLPSConversor;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.SegmentConverter;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.util.exception.ConversionException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SensorDataProcessorService {

  @Inject private SegmentRepository segmentRepository;
  @Inject private LocationRepository locationRepository;
  @Inject private EmployeeRepository employeeRepository;
  @Inject private TaxonomyRepository taxonomyRepository;
  private AnnotatorService annotatorService = new AnnotatorService();
  @Inject private SegmentConverter segmentConverter;
  @Inject private ActivityCategoryRepository activityCategoryRepository;
  @Inject private AnnotatorLPSConversor annotatorLPSConversor;
  @Inject private ActivityRepository activityRepository;

  public String processSensorData(Boolean debug)
      throws JsonParseException, JsonMappingException, IOException, ConversionException {
    String executionLog = ""; // Variable with execution log for debug mode
    ObjectMapper mapper = new ObjectMapper(); // JSON converter
    List<Employee> employeeList = employeeRepository.findAll();
    List<ActivityCategory> activityCategories = activityCategoryRepository.findAll();
    for (Employee employee : employeeList) {
      // For each worker journey since last processed time until now, retrieve data and run process
      final LocalTime journeyStart = LocalTime.of(0, 0); // Mock of journey start
      final LocalTime journeyEnd = LocalTime.of(23, 59); // Mock of journey end
      LocalDateTime initialTime; // From time
      LocalDateTime currentFinal; // End of journey time
      LocalDateTime finalTime = LocalDateTime.now();
      List<Segment> segmentList =
          segmentRepository.findByEmployeeAndCreationDateAfterOrderByStartTime(
              employee); // Not Processed Segments
      List<Location> locationList =
          locationRepository.findByEmployeeAndCreationDateAfterOrderByTime(
              employee); // Not Processed Locations
      Taxonomy taxonomy = taxonomyRepository.findByHasEmployee(employee);

      if (debug) {
        executionLog =
            executionLog
                + "============SEGMENTOS A PROCESAR PARA EL EMPLEADO: "
                + employee.getId()
                + "============\n";
        executionLog = executionLog + mapper.writeValueAsString(segmentList) + "\n";
        executionLog =
            executionLog
                + "============LOCALIZACIONES A PROCESAR PARA EL EMPLEADO: "
                + employee.getId()
                + "============\n";
        executionLog = executionLog + mapper.writeValueAsString(locationList) + "\n";
      }

      segmentList.removeIf(
          segment ->
              segment
                  .getStartTime()
                  .isAfter(segment.getEndTime())); // Discarding bad timed segments

      if (taxonomy == null || segmentList.size() < 1) continue;

      if (employee.getLastProcessed() != null) {
        // last process ran inside a worker journey
        if (employee.getLastProcessed().toLocalTime().isAfter(journeyStart)
            && employee.getLastProcessed().toLocalTime().isBefore(journeyEnd)) {
          initialTime = employee.getLastProcessed(); // Starting at lastProcessed moment
          currentFinal = getJourneyEnd(initialTime.toLocalDate(), journeyStart, journeyEnd);
        }
        // last process ran before first day worker journey
        else if (employee
            .getLastProcessed()
            .isAfter(LocalDateTime.of(employee.getLastProcessed().toLocalDate(), journeyStart))) {
          initialTime =
              employee
                  .getLastProcessed()
                  .withHour(journeyStart.getHour())
                  .withMinute(
                      journeyStart.getMinute()); // Starting at lastProcessed day worker journey
          currentFinal = getJourneyEnd(initialTime.toLocalDate(), journeyStart, journeyEnd);
        }
        // last process ran after first day worker journey
        else {
          initialTime =
              employee
                  .getLastProcessed()
                  .plusDays(1)
                  .withHour(journeyStart.getHour())
                  .withMinute(journeyStart.getMinute()); // Starting at next day worker journey
          currentFinal = getJourneyEnd(initialTime.toLocalDate(), journeyStart, journeyEnd);
        }
      }
      // Employee never processed
      else {
        initialTime =
            LocalDateTime.of(
                segmentList.get(0).getStartTime().toLocalDate(),
                journeyStart); // First day with data
        currentFinal = getJourneyEnd(initialTime.toLocalDate(), journeyStart, journeyEnd);
      }
      while (initialTime.isBefore(finalTime)) {
        List<Segment> journeySegmentList =
            filterSegmentList(segmentList, initialTime, currentFinal);
        List<Location> journeyLocationList =
            filterLocationList(locationList, initialTime, currentFinal);
        List<TaxonomySegment> taxonomySegmentList =
            segmentConverter.toTaxonomySegmentList(journeySegmentList, journeyLocationList);
        List<TaxonomySegment> taxonomySegmentListCopy =
            new ArrayList<>(taxonomySegmentList); // Copy to be printed in debug mode

        // Converting from annotator activities to GEMA LPS activities
        List<AnnotatorSegment> annotatorSegments =
            taxonomySegmentList.stream()
                .map(s -> annotatorLPSConversor.lpsSegmentToAnnotatorSegment(s))
                .collect(Collectors.toList());
        AnnotatorEmployee annotatorEmployee =
            annotatorLPSConversor.lpsEmployeeToAnnotatorEmployee(employee);
        AnnotatorTaxonomyNode annotatorTaxonomyNode =
            annotatorLPSConversor.lpsTaxonomyNodeToAnnotatorTaxonomyNode(taxonomy.getRoot());
        List<AnnotatorActivityCategory> annotatorActivityCategories =
            activityCategories.stream()
                .map(ac -> annotatorLPSConversor.lpsActivityCategoryToAnnotatorActivityCategory(ac))
                .collect(Collectors.toList());

        // Converting from annotator activities to GEMA LPS activities
        List<Activity> annotatedActivities =
            annotatorService
                .annotate(
                    annotatorSegments,
                    annotatorEmployee,
                    annotatorTaxonomyNode,
                    annotatorActivityCategories)
                .stream()
                .map(a -> annotatorLPSConversor.annotatorActivityToLpsActivity(a))
                .collect(Collectors.toList());

        // Saving generated activities
        annotatedActivities.forEach(a -> activityRepository.save(a));

        if (debug) {
          executionLog =
              executionLog
                  + "============SEGMENTOS PROCESADOS POR LA MAQUINA DE ESTADOS PARA EL EMPLEADO: "
                  + employee.getId()
                  + " EN LA JORNADA: "
                  + initialTime
                  + "-"
                  + currentFinal
                  + "============\n";
          executionLog = executionLog + mapper.writeValueAsString(taxonomySegmentListCopy) + "\n";
          executionLog =
              executionLog
                  + "============ACTIVIDADES ANOTADAS PARA EL EMPLEADO: "
                  + employee.getId()
                  + " EN LA JORNADA: "
                  + initialTime
                  + "-"
                  + currentFinal
                  + "============\n";
          executionLog = executionLog + mapper.writeValueAsString(annotatedActivities) + "\n";
        }
        // Update segment tag??
        initialTime =
            initialTime
                .plusDays(1)
                .withHour(journeyStart.getHour())
                .withMinute(journeyStart.getMinute());
        currentFinal = currentFinal.plusDays(1);
      }
      employee.setLastProcessed(finalTime); // Last instant processed
      employeeRepository.save(employee);
    }
    return executionLog;
  }

  private List<Segment> filterSegmentList(
      List<Segment> segmentList, LocalDateTime start, LocalDateTime end) {
    return segmentList.stream()
        .filter(
            segment -> segment.getStartTime().isAfter(start) && segment.getEndTime().isBefore(end))
        .collect(Collectors.toList());
  }

  private List<Location> filterLocationList(
      List<Location> locationList, LocalDateTime start, LocalDateTime end) {
    return locationList.stream()
        .filter(location -> location.getTime().isAfter(start) && location.getTime().isBefore(end))
        .collect(Collectors.toList());
  }

  private LocalDateTime getJourneyEnd(LocalDate day, LocalTime journeyStart, LocalTime journeyEnd) {
    if (journeyEnd.isBefore(journeyStart)) {
      return LocalDateTime.of(day, journeyEnd).plusDays(1);
    } else {
      return LocalDateTime.of(day, journeyEnd);
    }
  }
}

/*% } %*/
