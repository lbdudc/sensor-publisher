/*% if (feature.GUI_StaticPages) { %*/
package es.udc.lbd.gema.lps.component.static_page.model.web.rest;

import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPage;
import es.udc.lbd.gema.lps.component.static_page.model.service.StaticPageDTO;
import es.udc.lbd.gema.lps.component.static_page.model.service.StaticPageService;
import es.udc.lbd.gema.lps.model.domain.language.Language;
import es.udc.lbd.gema.lps.web.rest.custom.ValidationErrorUtils;
import es.udc.lbd.gema.lps.web.rest.util.HeaderUtil;
import es.udc.lbd.gema.lps.web.rest.util.PaginationUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.Valid;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(StaticPageResource.STATIC_PAGE_RESOURCE_URL)
public class StaticPageResource {
  public static final String STATIC_PAGE_RESOURCE_URL = "/api/entities/statics";

  private static final Logger logger = LoggerFactory.getLogger(StaticPageResource.class);

  @Inject private StaticPageService staticPageService;

  @GetMapping
  public ResponseEntity<Page<StaticPageDTO>> getAll(
    @PageableDefault(page = 0, size = 100000, sort = "id") Pageable pageable,
    @RequestParam(value = "filters", required = false) List<String> filters,
    @RequestParam(value = "search", required = false) String search,
    @Spec(path = "id", params = "ids", paramSeparator = ',', spec = In.class)
      Specification<StaticPage> idsSpec,
    @RequestParam(value = "language", required = true) String language) {

    Language languageValue = Language.valueOf(language.toUpperCase());

    Page<StaticPageDTO> page =
      staticPageService.getAll(pageable, filters, search, idsSpec, languageValue);

    HttpHeaders headers =
      PaginationUtil.generatePaginationHttpHeaders(page, STATIC_PAGE_RESOURCE_URL);
    return new ResponseEntity<>(page, headers, HttpStatus.OK);
  }

  @GetMapping("/{definedId}")
  public ResponseEntity<StaticPageDTO> getById(@PathVariable String definedId) {

    StaticPageDTO staticPageDTO = staticPageService.getByDefinedId(definedId);

    if (staticPageDTO != null) {
      return new ResponseEntity<>(staticPageDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/{definedId}/{language}")
  public ResponseEntity<StaticPageDTO> getTranslationById(@PathVariable String definedId, @PathVariable String language) {

    Language langValue = Language.valueOf(language.toUpperCase());
    StaticPageDTO staticPageDTO = staticPageService.getTranslationByDefinedId(definedId, langValue);

    if (staticPageDTO != null) {
      return new ResponseEntity<>(staticPageDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
  /*% if (feature.GUI_SP_Management) { %*/

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody StaticPageDTO staticPageDTO, Errors errors)
      throws URISyntaxException {

    if (staticPageDTO.getId() != null) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError("static_editor.errors.id_exists", null))
          .body(null);
    }

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }

    if (staticPageService.getByDefinedId(staticPageDTO.getDefinedId()) != null) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError("static_editor.errors.defined_id_already_exists", null))
          .body(null);
    }

    StaticPageDTO result = staticPageService.create(staticPageDTO);

    return ResponseEntity.created(
            new URI(String.format("%s/%s", STATIC_PAGE_RESOURCE_URL, result.getId())))
        .body(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable Long id, @Valid @RequestBody StaticPageDTO staticPageDTO, Errors errors) {

    if (!id.equals(staticPageDTO.getId())) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError("entity.static_page.error.id-not-exists", null))
          .body(null);
    }

    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }

    if (!staticPageService.get(id).getDefinedId().equals(staticPageDTO.getDefinedId())) {
      return ResponseEntity.badRequest()
          .headers(HeaderUtil.createError("entity.static_page.error.mutating_defined_id", null))
          .body(null);
    }

    StaticPageDTO result = staticPageService.update(staticPageDTO);
    return ResponseEntity.ok().body(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      staticPageService.delete(id);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .headers(HeaderUtil.createError("psql.exception", null))
        .build();
    }
    return ResponseEntity.ok().build();
  }
  /*% } %*/
}
/*% } %*/
