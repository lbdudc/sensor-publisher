/*% if (feature.MWM_PM_Clients) { %*/
package es.udc.lbd.gema.lps.component.staff_crud;

import es.udc.lbd.gema.lps.component.gema.model.service.ClientService;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientDTO;
import es.udc.lbd.gema.lps.component.gema.model.service.dto.ClientFullDTO;
import es.udc.lbd.gema.lps.web.rest.custom.ValidationErrorUtils;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ClientStaffResource.CLIENT_RESOURCE_URL)
public class ClientStaffResource {

  public static final String CLIENT_RESOURCE_URL = "/api/staff_crud/clients";

  private static final Logger log = LoggerFactory.getLogger(ClientStaffResource.class);

  @Inject private ClientService clientService;

  @GetMapping
  public ResponseEntity<Page<ClientDTO>> findAll(
    @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
    @RequestParam(value = "filters", required = false) List<String> filters) {

    Page<ClientDTO> page = clientService.getAll(pageable, filters, null);

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

  @PostMapping
  public ResponseEntity<?> createClient(@Valid @RequestBody ClientFullDTO clientDTO, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    ClientFullDTO clientCreatedDTO = clientService.createClient(clientDTO);
    return new ResponseEntity<>(clientCreatedDTO, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateClient(
    @PathVariable Long id, @Valid @RequestBody ClientFullDTO clientDTO, Errors errors) {
    if (id != clientDTO.getId()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    ClientFullDTO clientUpdated = clientService.updateClient(clientDTO);
    if (clientUpdated != null) {
      return new ResponseEntity<>(clientUpdated, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}/active")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    Long idDeleted = clientService.delete(id);
    if (idDeleted != null) {
      return new ResponseEntity<>(HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping("/{id}/active")
  public ResponseEntity<?>  activate(@PathVariable Long id) {
	Long idActivated = clientService.activate(id);
	if (idActivated != null) {
	  return new ResponseEntity<>(HttpStatus.OK);
	 } else {
	   return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	 }
  }
}
/*% } %*/
