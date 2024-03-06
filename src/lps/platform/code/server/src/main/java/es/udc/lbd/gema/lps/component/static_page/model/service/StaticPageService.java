/*% if (feature.GUI_StaticPages) { %*/
/*% var canDeleteHomePage = !(data.statics && data.statics.find(s => s.id == 'Home')); %*/
package es.udc.lbd.gema.lps.component.static_page.model.service;

import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPage;
import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPageI18n;
import es.udc.lbd.gema.lps.component.static_page.model.repository.StaticPageRepository;
import es.udc.lbd.gema.lps.component.static_page.model.web.rest.StaticPageSpecification;
import es.udc.lbd.gema.lps.model.domain.language.Language;
  /*% if (feature.GUI_SP_Management && !canDeleteHomePage) { %*/
import es.udc.lbd.gema.lps.model.service.exceptions.DeleteHomePageException;
  /*% } %*/
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
  /*% if (feature.UserManagement) { %*/
import es.udc.lbd.gema.lps.model.service.UMUserService;
  /*% } %*/
import es.udc.lbd.gema.lps.web.rest.util.specification_utils.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.inject.Inject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Service
@Transactional
public class StaticPageService {

  @Inject private StaticPageRepository staticPageRepository;
  /*% if (feature.UserManagement) { %*/

  @Inject private UMUserService userService;
  /*% } %*/

  /**
   * Returns all static pages that meet the search criteria
   * @param Contains all information about number page, items per page, order, ...
   * @param filters Static filters to apply ("{attribute}:{value}" format) [does not apply together with search]
   * @param search Text to find in all attributes of the entity
   * @param idsSpec Specification that filter static pages by ID's
   * @param language Language in which you want to obtain the static pages
   * @return Static pages with their content in the specified language
   */
  public Page<StaticPageDTO> getAll (
    Pageable pageable,
    List<String> filters,
    String search,
    Specification idsSpec,
    Language language) {

    Page<StaticPage> page;
    // Specification that filters static pages by language
    Specification langSpec = StaticPageSpecification.seachByLanguage(language);

    if (search == null || search.isEmpty()) {
      page =
        staticPageRepository.findAll(
          langSpec.and(idsSpec).and(SpecificationUtil.getSpecificationFromFilters(filters, false)),
          pageable);
    } else {
      page =
        staticPageRepository.findAll(
          langSpec.and(idsSpec).and(StaticPageSpecification.searchAll(search)), pageable);
    }

    return page.map(el -> convertToStaticPageDTOTranslated(el, language));
  }

  private StaticPageDTO convertToStaticPageDTOTranslated (
    final StaticPage staticPage, Language language) {
    List<StaticPageI18n> translation =
      staticPage.getTranslations().stream()
        .filter(t -> t.getLanguage().equals(language))
        .collect(Collectors.toList());
    if (translation.size() > 0) {
      staticPage.setTranslations(translation);
      return new StaticPageDTO(staticPage);
    }
    return null;
  }

  public StaticPageDTO get (Long id) {
    StaticPage staticPage = staticPageRepository.findById(id).orElse(null);
    if (staticPage != null) return new StaticPageDTO(staticPage);
    else return null;
  }

  public StaticPageDTO getByDefinedId(String definedId) {
    StaticPage staticPage = staticPageRepository.findByDefinedId(definedId).orElse(null);
    if (staticPage != null) return new StaticPageDTO(staticPage);
    else return null;
  }

  public StaticPageDTO getTranslationByDefinedId(String definedId, Language language) {
    StaticPage staticPage = staticPageRepository.findByDefinedId(definedId).orElse(null);
    if (staticPage != null) return convertToStaticPageDTOTranslated(staticPage, language);
    else return null;
  }
  /*% if (feature.GUI_SP_Management) { %*/

  public StaticPageDTO create (StaticPageDTO staticPageDTO) {
    LocalDate d = LocalDate.now();
    StaticPage staticPageBD = new StaticPage();
    List<StaticPageI18n> translations =
        staticPageDTO.getTranslations().stream()
            .map(
                t ->
                    new StaticPageI18n(
                        t.getId(), t.getLanguage(), t.getBody(), t.getDescription(), staticPageBD))
            .collect(Collectors.toList());

    staticPageBD.setDefinedId(staticPageDTO.getDefinedId());
    staticPageBD.setCreated_date(d);
    staticPageBD.setModified_date(d);
    staticPageBD.setTranslations(translations);
    staticPageBD.setAuthor(userService.getUserWithAuthorities());

    StaticPage result = staticPageRepository.save(staticPageBD);
    return new StaticPageDTO(result);
  }

  public StaticPageDTO update (StaticPageDTO staticPageDTO){
    LocalDate d = LocalDate.now();

    Optional<StaticPage> optionalStaticPageBD = staticPageRepository.findById(staticPageDTO.getId());

    if (optionalStaticPageBD.isPresent()) {
      StaticPage staticPageBD = optionalStaticPageBD.get();
      List<StaticPageI18n> translations =
        staticPageDTO.getTranslations().stream()
            .map(
                t ->
                    new StaticPageI18n(
                        t.getId(), t.getLanguage(), t.getBody(), t.getDescription(), staticPageBD))
            .collect(Collectors.toList());

      staticPageBD.setDefinedId(staticPageDTO.getDefinedId());
      staticPageBD.setModified_date(d);
      staticPageBD.setTranslations(translations);

      StaticPage result = staticPageRepository.save(staticPageBD);
      return new StaticPageDTO(result);
    }
    return null;
  }
  public void delete(Long id) throws NotFoundException/*% if (!canDeleteHomePage) { %*/, DeleteHomePageException/*% } %*/ {
    StaticPage staticPage =
      staticPageRepository.findById(id).orElseThrow(() -> new NotFoundException("Static page with ID " + id + " not found"));
    /*% if (!canDeleteHomePage) { %*/
    if (staticPage.getDefinedId().equals("Home")) {
      throw new DeleteHomePageException();
    }
    /*% } %*/
    staticPageRepository.deleteById(id);
  }
  /*% } %*/
}
/*% } %*/
