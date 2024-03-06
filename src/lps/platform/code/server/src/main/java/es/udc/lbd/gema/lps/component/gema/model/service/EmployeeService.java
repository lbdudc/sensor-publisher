/*% if (feature.MWM_M_Employee) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.repository.EmployeeRepository;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeFullDTO;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.spatial_specifications.WithinSpecification;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import es.udc.lbd.gema.lps.config.Properties;
  /*% if (feature.MWM_EmployeeAuthentication) { %*/
import es.udc.lbd.gema.lps.model.domain.user_management.Authority;
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
import es.udc.lbd.gema.lps.model.service.UMUserService;
import es.udc.lbd.gema.lps.security.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
  /*% } %*/
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/*% if (feature.MWM_TE_VisitsRecord || feature.MWM_TE_ActivitiesRecord) { %*/
import java.time.LocalDate;
/*% } %*/
/*% if (feature.MWM_TE_VisitsRecord) { %*/
import es.udc.lbd.gema.lps.component.gema.model.domain.PlannedEventState;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeStatisticsDTO;
/*% } %*/
/*% if (feature.MWM_TE_ActivitiesRecord) { %*/
import java.time.LocalDateTime;
import java.time.LocalTime;
/*% } %*/

@Service
@Transactional
public class EmployeeService {

  @Inject private EmployeeRepository employeeRepository;

  /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @Inject private UMUserService umUserService;
  /*% } %*/

  @Inject private Properties properties;

  /**
   *
   * @param pageable Contains all information about number page, items per page, order, ...
   * @param idsSpec Specification that filter employees by ID's [exclusive with filters]
   * @param filters Key-value pairs for filtering employees
   * @param excluding When true, data must fit at least one of indicated 'filters'; when false, data
   *     must fit all of them
   * @return A filtered employees page
   */
  public Page<EmployeeDTO> getAll(
    Pageable pageable, Specification<Employee> idsSpec, List<String> filters, Boolean excluding) {

    Page<Employee> page;

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      if (filters != null) {
        filters.add("id:" + umUser.getEmployee().getId().toString());
      } else {
        filters = Arrays.asList("id:" + umUser.getEmployee().getId().toString());
      }

      page =
        employeeRepository.findAll(
          SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);

      return page.map(employee -> new EmployeeDTO(employee));
    }
    /*% } %*/

    // If ids present, filter only by ID and all elements are sent
    if (idsSpec != null) {
      page = employeeRepository.findAll(idsSpec, pageable);
    } else {
      page =
        employeeRepository.findAll(
          SpecificationUtil.getSpecificationFromFilters(filters, false), pageable);
    }

    return page.map(employee -> new EmployeeDTO(employee));
  }

  public List<EmployeeDTO> findAllByFullNameAndWithoutIds(List<Long> ids, String fullName) {
    String fullNameSearch = fullName != null ? '%' + fullName.toLowerCase() + '%' : null;
    // Sets ids list to null if it is empty
    ids = ids.isEmpty() ? null : ids;

    List<Employee> employees =
      employeeRepository.findByFullNameInAndIdNotInOrderByFullName(ids, fullNameSearch);

    return employees.stream()
      .map(employee -> new EmployeeDTO(employee))
      .collect(Collectors.toList());
  }

  public List<EmployeeDTO> findAll(Specification spec, Geometry bbox) {
    // Fixing SRID
    if (bbox != null) {
      bbox.setSRID(properties.getGis().getDefaultSrid());
    }

    Specification bboxSpec = new WithinSpecification(bbox, "location");
    GenericSpecification genericSpecification = new GenericSpecification();

    // We only want active employees
    genericSpecification.add(
      new SearchCriteria("active", true, SearchOperation.EQUAL, LogicalOperation.AND));

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      genericSpecification.add(
        new SearchCriteria(
          "id", umUser.getEmployee().getId(), SearchOperation.EQUAL, LogicalOperation.AND));
    }
    /*% } %*/

    List<Employee> result =
      employeeRepository.findAll(bboxSpec.and(spec)/*% if (feature.MWM_EmployeeAuthentication) { %*/.and(genericSpecification)/*% } %*/, Sort.by("fullName"));

    return result.stream().map(employee -> new EmployeeDTO(employee)).collect(Collectors.toList());
  }

  private List<EmployeeDTO> findAllById(List<Long> ids) {
    List<Employee> employees = employeeRepository.findAllById(ids);
    List<EmployeeDTO> employeesDTO = employees.stream().map(employee -> new EmployeeDTO(employee)).collect(Collectors.toList());
    /*% if (feature.MWM_EmployeeAuthentication) { %*/

    UMUser umUser = umUserService.getUserWithAuthorities(); // Current user
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      return employeesDTO.stream()
        .filter(employeeDTO -> employeeDTO.getId() == umUser.getEmployee().getId())
        .collect(Collectors.toList());
    }
    /*% } %*/

    return employeesDTO;
  }
  /*% if (feature.MWM_PM_Employees || feature.MWM_VisitSchedule || feature.MWM_TE_Realtime || feature.MWM_TE_ActivitiesRecord) { %*/

  public EmployeeFullDTO findById(Long id) /*% if (feature.MWM_EmployeeAuthentication) { %*/ throws ForbiddenUserException /*% } %*/ {
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    UMUser umUser = umUserService.getUserWithAuthorities();
    if (!SecurityUtils.isCurrentUserInRole("ROLE_ADMIN") && umUser.getEmployee() != null) {
      if (umUser.getEmployee().getId() != id) throw new ForbiddenUserException();
    }

    /*% } %*/
    Employee employee = employeeRepository.findById(id).orElse(null);
    if (employee != null) {
      return new EmployeeFullDTO(employee);
    } else {
      return null;
    }
  }
  /*% } %*/
  /*% if (feature.MWM_PM_Employees || feature.MWM_VisitSchedule) { %*/

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public EmployeeFullDTO createEmployee(EmployeeFullDTO employeeDTO) {
    Employee employee = new Employee();

    employee.setEmail(employeeDTO.getEmail());
    employee.setFullName(employeeDTO.getFullName());
    employee.setLabel(employeeDTO.getLabel());
    employee.setLocation(employeeDTO.getLocation());
    employee.setPhone(employeeDTO.getPhone());
    employee.setActive(true); // Active by default

    // Fixing location SRID
    if (employee.getLocation() != null)
      employee.getLocation().setSRID(properties.getGis().getDefaultSrid());

    Employee employeeBd = employeeRepository.saveAndFlush(employee);
    return new EmployeeFullDTO(employeeBd);
  }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public EmployeeFullDTO updateEmployee(EmployeeFullDTO employeeDTO) {
    Employee employee = employeeRepository.findById(employeeDTO.getId()).orElse(null);

    if (employee == null) {
      return null;
    }

    employee.setEmail(employeeDTO.getEmail());
    employee.setFullName(employeeDTO.getFullName());
    employee.setLabel(employeeDTO.getLabel());
    employee.setLocation(employeeDTO.getLocation());
    employee.setPhone(employeeDTO.getPhone());
    //employee.active can't be mutated outside activate/delete methods

    // Fixing location SRID
    if (employee.getLocation() != null)
      employee.getLocation().setSRID(properties.getGis().getDefaultSrid());

    Employee employeeBd = employeeRepository.save(employee);
    return new EmployeeFullDTO(employeeBd);
  }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public Long delete(Long id) {
    Employee employee = employeeRepository.findById(id).orElse(null);
    if (employee != null) {
      employee.setActive(false); // Logic delete
      employeeRepository.save(employee);
      return id;
    } else {
      return null;
    }
  }

    /*% if (feature.MWM_EmployeeAuthentication) { %*/
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    /*% } %*/
  public Long activate(Long id) {
    Employee employee = employeeRepository.findById(id).orElse(null);
    if (employee != null) {
      employee.setActive(true);
      employeeRepository.save(employee);
      return id;
    } else {
      return null;
    }
  }
  /*% } %*/
  /*% if (feature.MWM_TE_ActivitiesRecord) { %*/
  public List<EmployeeDTO> findByHasActivitiesBetween(LocalDate start, LocalDate end) {
    // Necessary conversion of LocalDate to LocalDateTime in order to filter activities by its LocalDateTime fields
    LocalDateTime startTime = start.atStartOfDay();
    LocalDateTime endTime = end != null ? end.atTime(LocalTime.MAX) : start.atTime(LocalTime.MAX);
    List<Employee> employees = employeeRepository.findByHasActivitiesBetween(startTime, endTime);
    List<EmployeeDTO> employeesDTO =
        employees.stream().map(employee -> new EmployeeDTO(employee)).collect(Collectors.toList());
    return employeesDTO;
  }
  /*% } %*/
  /*% if (feature.MWM_TE_VisitsRecord) { %*/
  public List<EmployeeDTO> findByHasVisitsBetween(LocalDate start, LocalDate end, PlannedEventState state) {
    List<Employee> employees = employeeRepository.findByHasVisitsBetween(start, end, state);
    List<EmployeeDTO> employeesDTO =
        employees.stream().map(employee -> new EmployeeDTO(employee)).collect(Collectors.toList());
    return employeesDTO;
  }

  public List<EmployeeStatisticsDTO> getVisitsStatistics(
      List<Long> clients, List<Long> employees, LocalDate start, LocalDate end, String type) {
    if (type != null && type.equalsIgnoreCase("In")) end = start;

    return employeeRepository.getVisitsStatistics(clients, employees, start, end, PlannedEventState.DONE);
  }
  /*% } %*/
}
/*% } %*/
