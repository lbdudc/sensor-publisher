/*% if (feature.MWM_EmployeeAuthentication) { %*/
package es.udc.lbd.gema.lps.web.rest.custom.user_management;

import es.udc.lbd.gema.lps.component.gema.model.service.EmployeeService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.EmployeeDTO;
import es.udc.lbd.gema.lps.model.repository.user_management.UMUserRepository;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UMEmployeeResource.EMPLOYEE_RESOURCE_URL)
public class UMEmployeeResource {

  public static final String EMPLOYEE_RESOURCE_URL = "/api/user-management/employees";

  private static final Logger log = LoggerFactory.getLogger(UMEmployeeResource.class);

  @Inject private EmployeeService employeeService;

  @Inject private UMUserRepository userRepository;

  @GetMapping
  public ResponseEntity<Page<EmployeeDTO>> findAll(
    @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable,
    @RequestParam(value = "filters", required = false) List<String> filters,
    @Spec(path = "id", params = "ids", paramSeparator = ',', spec = In.class)
      Specification idsSpecification,
    @RequestParam(value = "exclusiveFilters", required = false, defaultValue = "false")
      Boolean exclusiveFilters) {

    Page<EmployeeDTO> page =
      employeeService.getAll(pageable, idsSpecification, filters, exclusiveFilters);

    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, EMPLOYEE_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/without-user-associated")
  public ResponseEntity<List<EmployeeDTO>> findAllWithoutIds(
    @RequestParam(value = "fullName", required = false) String fullName,
    @RequestParam(value = "unfilteredEmployee", required = false) Long unfilteredEmployee) {

    List<Long> usersIds =
      userRepository.findAll().stream()
        .filter(
          user ->
            user.getEmployee() != null && user.getEmployee().getId() != unfilteredEmployee)
        .map(user -> user.getEmployee().getId())
        .collect(Collectors.toList());

    List<EmployeeDTO> employeesDTO =
      employeeService.findAllByFullNameAndWithoutIds(usersIds, fullName);

    return new ResponseEntity<>(employeesDTO, HttpStatus.OK);
  }
}
/*% } %*/
