/*% if (feature.MWM_VisitSchedule) { %*/
package es.udc.lbd.gema.lps.component.visit_schedule;

import es.udc.lbd.gema.lps.component.gema.model.service.ClientService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientFullDTO;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import java.util.List;
import javax.inject.Inject;
import net.kaczmarzyk.spring.data.jpa.domain.*;
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

@RestController (value = "ASDFCONT")
@RequestMapping(ClientStaffResource.CLIENT_RESOURCE_URL)
public class ClientStaffResource {

  public static final String CLIENT_RESOURCE_URL = "/api/planned_event_crud/clients";

  private static final Logger log = LoggerFactory.getLogger(ClientStaffResource.class);

  @Inject private ClientService clientService;

  @GetMapping
  public ResponseEntity<Page<ClientDTO>> findAll(
    @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
    @RequestParam(value = "filters", required = false) List<String> filters,
    @Spec(path = "id", params = "ids", paramSeparator = ',', spec = In.class)
      Specification idsSpecification) {

    Page<ClientDTO> page = clientService.getAll(pageable, filters, idsSpecification);

    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, CLIENT_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClientFullDTO> findById(@PathVariable Long id) {
    ClientFullDTO clientFullDTO = clientService.findById(id);
    if (clientFullDTO != null) {
      return new ResponseEntity<>(clientFullDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

}
/*% } %*/
