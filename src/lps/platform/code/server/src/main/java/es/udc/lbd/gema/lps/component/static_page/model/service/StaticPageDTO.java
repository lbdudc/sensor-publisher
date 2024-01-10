/*% if (feature.GUI_StaticPages) { %*/
package es.udc.lbd.gema.lps.component.static_page.model.service;

import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPage;
  /*% if (feature.UserManagement) { %*/
import es.udc.lbd.gema.lps.model.service.dto.UMUserJSON;
  /*% } %*/
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

public class StaticPageDTO {

  private Long id;

  @NotEmpty
  private String definedId;

  private LocalDate created_date;

  private LocalDate modified_date;

  @NotEmpty
  @Valid 
  private List<StaticPageI18nDTO> translations;
  /*% if (feature.UserManagement) { %*/

  private UMUserJSON author;
  /*% } %*/

  public StaticPageDTO() {}

  public StaticPageDTO(StaticPage staticPage) {
    this.id = staticPage.getId();
    this.definedId = staticPage.getDefinedId();
    this.created_date = staticPage.getCreated_date();
    this.modified_date = staticPage.getModified_date();
    if (staticPage.getTranslations() != null) { // Prevent NullPointerException
      this.translations =
          staticPage.getTranslations().stream()
              .map(t -> new StaticPageI18nDTO(t))
              .collect(Collectors.toList());
    }
    /*% if (feature.UserManagement) { %*/
    if (staticPage.getAuthor() != null) { // Prevent NullPointerException
      this.author = new UMUserJSON(staticPage.getAuthor());
    }
    /*% } %*/
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDefinedId() {
    return definedId;
  }
  
  public void setDefinedId(String definedId) {
    this.definedId = definedId;
  }
  /*% if (feature.UserManagement) { %*/

  public UMUserJSON getAuthor() {
    return author;
  }

  public void setAuthor(UMUserJSON author) {
    this.author = author;
  }
  /*% } %*/

  public LocalDate getCreated_date() {
    return created_date;
  }

  public void setCreated_date(LocalDate created_date) {
    this.created_date = created_date;
  }

  public LocalDate getModified_date() {
    return modified_date;
  }

  public void setModified_date(LocalDate modified_date) {
    this.modified_date = modified_date;
  }

  public List<StaticPageI18nDTO> getTranslations() {
    return translations;
  }

  public void setTranslations(List<StaticPageI18nDTO> translations) {
    this.translations = translations;
  }
}
/*% } %*/
