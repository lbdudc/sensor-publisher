/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs;

import es.udc.lbd.gema.lps.component.gtfs.model.service.AgencyService;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.AgencyDTO;
import es.udc.lbd.gema.lps.component.gtfs.model.service.dto.AgencyFullDTO;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.model.service.exceptions.OperationNotAllowedException;
import es.udc.lbd.gema.lps.web.rest.custom.ValidationErrorUtils;
import es.udc.lbd.gema.lps.web.rest.util.HeaderUtil;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AgencyResource.AGENCY_RESOURCE_URL)
public class AgencyResource {
  public static final String AGENCY_RESOURCE_URL = "/api/gtfs/agency";

  private static final Logger logger = LoggerFactory.getLogger(AgencyResource.class);

  @Inject private AgencyService agencyService;

  /**
   * Get entities in pages<br>
   *
   * <p>If no <code>pageNum</code> is provided, then all results will be returned in one page
   *
   * @param pageable Contains all information about number page, items per page, order, ...
   * @param search Contains a text that will be searched in all the properties of the entity
   * @param filters Static filters to apply
   * @return Paginated entities
   */
  @GetMapping
  public ResponseEntity<Page<AgencyDTO>> getAll(
      @PageableDefault(page = 0, size = 100000, sort = "agencyName") Pageable pageable,
      @RequestParam(value = "filters", required = false) List<String> filters,
      @RequestParam(value = "search", required = false) String search) {
    Page<AgencyDTO> page = agencyService.getAll(pageable, filters, search);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, AGENCY_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AgencyFullDTO> get(@PathVariable String id) {
    try {
      return new ResponseEntity<>(agencyService.get(id), HttpStatus.OK);
    } catch (NotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody AgencyFullDTO agency, Errors errors)
      throws URISyntaxException {

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    try {
      AgencyFullDTO result = agencyService.create(agency);
      return ResponseEntity.created(
              new URI(String.format("%s/%s", AGENCY_RESOURCE_URL, result.getAgencyId())))
          .body(result);
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError(e.getMessage(), null))
          .body(null);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable String id, @Valid @RequestBody AgencyFullDTO agency, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }
    try {
      AgencyFullDTO result = agencyService.update(id, agency);
      return ResponseEntity.ok().body(result);
    } catch (OperationNotAllowedException e) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError(e.getMessage(), null))
          .body(null);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    try {
      agencyService.delete(id);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .headers(HeaderUtil.createError("psql.exception", null))
          .build();
    }

    return ResponseEntity.ok().build();
  }
}
/*% } %*/
