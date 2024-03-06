/*% if (feature.MWM_VisitSchedule) { %*/
package es.udc.lbd.gema.lps.component.visit_schedule;

import java.util.List;

import javax.inject.Inject;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.lbd.gema.lps.component.gema.model.domain.Employee;
import es.udc.lbd.gema.lps.component.gema.model.service.EmployeeService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeFullDTO;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
/*% if (feature.MWM_EmployeeAuthentication) { %*/

import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
/*% } %*/

@RestController (value = "PEEmployeeStaffController")
@RequestMapping(EmployeeStaffResource.EMPLOYEE_RESOURCE_URL)
public class EmployeeStaffResource {

  public static final String EMPLOYEE_RESOURCE_URL = "/api/planned_event_crud/employees";

  private static final Logger log = LoggerFactory.getLogger(EmployeeStaffResource.class);

  @Inject private EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<Page<EmployeeDTO>> findAll(
      @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
      @RequestParam(value = "filters", required = false) List<String> filters,
      @Spec(path = "id", params = "ids", paramSeparator = ',', spec = In.class)
        Specification<Employee> idsSpec,
      @RequestParam(value = "exclusiveFilters", required = false, defaultValue = "false")
        Boolean exclusiveFilters) {

    Page<EmployeeDTO> page = employeeService.getAll(pageable, idsSpec, filters, exclusiveFilters);

    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, EMPLOYEE_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeFullDTO> findById(@PathVariable Long id) {
    /*% if (feature.MWM_EmployeeAuthentication) { %*/
    EmployeeFullDTO employeeFullDTO;
    try {
      employeeFullDTO = employeeService.findById(id);
    } catch (ForbiddenUserException e) {
		  return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    /*% } else { %*/
    EmployeeFullDTO employeeFullDTO = employeeService.findById(id);
    /*% } %*/
    if (employeeFullDTO != null) {
      return new ResponseEntity<>(employeeFullDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

}
/*% } %*/
