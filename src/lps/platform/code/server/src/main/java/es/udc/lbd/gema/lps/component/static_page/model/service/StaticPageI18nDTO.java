/*% if (feature.GUI_StaticPages) { %*/
package es.udc.lbd.gema.lps.component.static_page.model.service;

import es.udc.lbd.gema.lps.model.domain.language.Language;
import es.udc.lbd.gema.lps.component.static_page.model.domain.StaticPageI18n;
import javax.validation.constraints.NotEmpty;

public class StaticPageI18nDTO {

  private Long id;

  @NotEmpty private String body;

  private String description;

  private Language language;

  public StaticPageI18nDTO() {}

  public StaticPageI18nDTO(StaticPageI18n staticPageI18n) {
    this.id = staticPageI18n.getId();
    this.body = staticPageI18n.getBody();
    this.description = staticPageI18n.getDescription();
    this.language = staticPageI18n.getLanguage();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }
}
/*% } %*/