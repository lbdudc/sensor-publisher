/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Segment;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Tag;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.SegmentRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.TagRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.SegmentDTO;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.SegmentGroupDTO;
import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.repository.EmployeeRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/*% if (feature.MWM_EmployeeAuthentication) { %*/
import es.udc.lbd.gema.lps.model.domain.user_management.Authority;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.service.UMUserService;
import es.udc.lbd.gema.lps.security.SecurityUtils;
import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
/*% } %*/

@Service
@Transactional
public class SegmentService {

  @Inject private SegmentRepository segmentRepository;

  @Inject private TagRepository tagRepository;

  @Inject private EmployeeRepository employeeRepository;

  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @Inject private UMUserService umUserService;
  /*% } %*/

  public List<SegmentDTO> createSegment(List<SegmentDTO> segmentDtoList)
      throws NoSuchElementException {
    List<SegmentDTO> bdSegmentDtoList = new ArrayList<>();

    for (SegmentDTO segmentDto : segmentDtoList) {
      SegmentDTO bdSegmentDto = createSegment(segmentDto);
      bdSegmentDtoList.add(bdSegmentDto);
    }

    return bdSegmentDtoList;
  }

  public SegmentDTO createSegment(SegmentDTO segmentDto) throws NoSuchElementException {
    Segment segment = new Segment();

    segment.setId(segmentDto.getId());
    segment.setStartTime(segmentDto.getStartTime());
    segment.setEndTime(segmentDto.getEndTime());
    segment.setCreationDate(LocalDateTime.now());
    Employee employee = employeeRepository.findById(segmentDto.getEmployee().getId()).get();
    segment.setEmployee(employee);
    Tag tag = tagRepository.findByNameIgnoreCase(segmentDto.getTag().getName());
    segment.setTag(tag);

    segment = segmentRepository.save(segment);
    return new SegmentDTO(segment);
  }

  public List<SegmentDTO> findAll(LocalDateTime startTime, LocalDateTime endTime, Long employeeId)
      throws NoSuchElementException {
    Employee employee = employeeRepository.findById(employeeId).get();
    return segmentRepository
        .findByStartTimeAfterAndEndTimeBeforeAndEmployeeOrderByEndTime(startTime, endTime, employee)
        .stream()
        .map(segment -> new SegmentDTO(segment))
        .collect(Collectors.toList());
  }

  public List<SegmentGroupDTO> findAllByGroupsOf(
      LocalDateTime startTime, LocalDateTime endTime, Long employeeId, Integer interval) /*% if (feature.MWM_EmployeeAuthentication) { %*/ throws ForbiddenUserException /*% } %*/ {
    // Initialize variables
    LocalDateTime currentStart = startTime;
    LocalDateTime currentEnd =
        currentStart.plusMinutes(interval).isBefore(endTime)
            ? currentStart.plusMinutes(interval)
            : endTime;
    List<SegmentGroupDTO> segmentGroupListDto = new ArrayList<SegmentGroupDTO>();
    SegmentGroupDTO currentGroup = new SegmentGroupDTO();
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities();
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      if (employeeId != umUser.getEmployee().getId()) throw new ForbiddenUserException();
    }
    /*% } %*/
    List<SegmentDTO> segmentListDto = findAll(startTime, endTime, employeeId);
    List<SegmentDTO> currentSegmentList = new ArrayList<>();

    if (segmentListDto.size() > 0) {
      currentStart = segmentListDto.get(0).getStartTime();
      currentEnd =
          currentStart.plusMinutes(interval).isAfter(segmentListDto.get(0).getEndTime())
              ? currentStart.plusMinutes(interval)
              : segmentListDto.get(0).getEndTime();
    }

    // Assuming simultaneous segments won't exist
    for (Iterator<SegmentDTO> iterator = segmentListDto.iterator(); iterator.hasNext(); ) {
      SegmentDTO segmentDTO = (SegmentDTO) iterator.next();

      // If segment doesn't match current group interval time, close interval and open a new one
      if (segmentDTO.getStartTime().isBefore(currentStart)
          || segmentDTO.getEndTime().isAfter(currentEnd)) {
        // Close and push interval if data
        if (currentSegmentList.size() > 0) {
          // Current interval closing
          currentGroup.setInitTime(currentStart);
          currentGroup.setEndTime(
              currentSegmentList.get(currentSegmentList.size() - 1).getEndTime());
          currentGroup.setSegments(currentSegmentList);
          segmentGroupListDto.add(currentGroup);
        }
        // Update interval times: new interval starts where old interval finishes
        currentStart = segmentDTO.getStartTime();
        currentEnd =
            currentStart.plusMinutes(interval).isAfter(segmentDTO.getEndTime())
                ? currentStart.plusMinutes(interval)
                : segmentDTO.getEndTime();
        // New interval and segment list
        currentGroup = new SegmentGroupDTO();
        currentSegmentList = new ArrayList<>();
      }
      currentSegmentList.add(segmentDTO);
    }
    if (currentSegmentList.size() > 0) {
      // Current interval closing
      currentGroup.setInitTime(currentStart);
      currentGroup.setEndTime(currentSegmentList.get(currentSegmentList.size() - 1).getEndTime());
      currentGroup.setSegments(currentSegmentList);
      segmentGroupListDto.add(currentGroup);
    }
    return segmentGroupListDto;
  }
}

/*% } %*/
