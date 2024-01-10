/*% if (feature.MWM_TA_SensorDataCollector) { %*/
package es.udc.lbd.gema.lps.component.sensor_data_collector.model.service;

import es.udc.lbd.gema.lps.component.sensor_data_collector.model.domain.Location;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.repository.LocationRepository;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.LocationDTO;
import es.udc.lbd.gema.lps.component.sensor_data_collector.model.service.dto.LocationGroupDTO;
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
public class LocationService {

  @Inject private LocationRepository locationRepository;

  @Inject private EmployeeRepository employeeRepository;

  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @Inject private UMUserService umUserService;
  /*% } %*/

  public List<LocationDTO> createLocation(List<LocationDTO> locationDtoList)
      throws NoSuchElementException {
    List<LocationDTO> bdLocationDtoList = new ArrayList<>();

    for (LocationDTO locationDto : locationDtoList) {
      LocationDTO bdLocationDto = createLocation(locationDto);
      bdLocationDtoList.add(bdLocationDto);
    }

    return bdLocationDtoList;
  }

  public LocationDTO createLocation(LocationDTO locationDto) throws NoSuchElementException {
    Location location = new Location();

    location.setId(locationDto.getId());
    location.setAccuracy(locationDto.getAccuracy());
    location.setAltitude(locationDto.getAltitude());
    location.setBearing(locationDto.getBearing());
    location.setPosition(locationDto.getPosition());
    location.setProvider(locationDto.getProvider());
    location.setSpeed(locationDto.getSpeed());
    location.setTime(locationDto.getTime());
    location.setCreationDate(LocalDateTime.now());
    Employee employee = employeeRepository.findById(locationDto.getEmployee().getId()).get();
    location.setEmployee(employee);

    location = locationRepository.save(location);
    return new LocationDTO(location);
  }

  public List<LocationDTO> findAll(LocalDateTime startTime, LocalDateTime endTime, Long employeeId)
      throws NoSuchElementException {
    Employee employee = employeeRepository.findById(employeeId).get();
    return locationRepository
        .findByTimeAfterAndTimeBeforeAndEmployeeOrderByTime(startTime, endTime, employee).stream()
        .map(location -> new LocationDTO(location))
        .collect(Collectors.toList());
  }

  public List<LocationGroupDTO> findAllByGroupsOf(
      LocalDateTime startTime, LocalDateTime endTime, Long employeeId, Integer interval) /*% if (feature.MWM_EmployeeAuthentication) { %*/ throws ForbiddenUserException /*% } %*/ {
    List<LocationGroupDTO> locationsGroups = new ArrayList<LocationGroupDTO>();
    LocalDateTime currentStart = startTime;
    LocalDateTime currentEnd =
        currentStart.plusMinutes(interval).isBefore(endTime) ? currentStart.plusMinutes(interval) : endTime;
    ;
    LocationGroupDTO currentGroup = new LocationGroupDTO();
    List<LocationDTO> currentLocationList = new ArrayList<>();
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities();
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      if (employeeId != umUser.getEmployee().getId()) throw new ForbiddenUserException();
    }
    /*% } %*/
    List<LocationDTO> locationListDto = findAll(startTime, endTime, employeeId);
    if (locationListDto.size() > 0) {
      currentStart = locationListDto.get(0).getTime();
      currentEnd =
          currentStart.plusMinutes(interval).isBefore(endTime) ? currentStart.plusMinutes(interval) : endTime;
    }
    for (Iterator<LocationDTO> iterator = locationListDto.iterator(); iterator.hasNext(); ) {
      LocationDTO locationDTO = (LocationDTO) iterator.next();
      if (locationDTO.getTime().isBefore(currentStart)
          || locationDTO.getTime().isAfter(currentEnd)) {
        // Push group
        currentGroup.setInitTime(currentStart);
        currentGroup.setEndTime(currentEnd);
        currentGroup.setLocations(currentLocationList);
        locationsGroups.add(currentGroup);
        // Update times
        currentStart = locationDTO.getTime();
        currentEnd =
            currentStart.plusMinutes(interval).isBefore(endTime) ? currentStart.plusMinutes(interval) : endTime;
        // Create a new group
        currentGroup = new LocationGroupDTO();
        currentLocationList = new ArrayList<LocationDTO>();
      }
      currentLocationList.add(locationDTO);
    }
    if (locationListDto.size() > 0) {
      currentGroup.setInitTime(currentStart);
      currentGroup.setEndTime(currentEnd);
      currentGroup.setLocations(currentLocationList);
      locationsGroups.add(currentGroup);
    }
    return locationsGroups;
  }
}

/*% } %*/
