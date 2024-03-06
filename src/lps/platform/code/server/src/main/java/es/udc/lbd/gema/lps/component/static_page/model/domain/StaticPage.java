/*% if (feature.GUI_StaticPages) { %*/
package es.udc.lbd.gema.lps.component.static_page.model.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;
/*% if (feature.UserManagement) { %*/
import es.udc.lbd.gema.lps.model.domain.user_management.UMUser;
/*% } %*/
import java.time.LocalDate;

@Entity
@Table(name = "static_page")
public class StaticPage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staticPageGen")
    @SequenceGenerator(name = "staticPageGen", sequenceName = "static_page_id_seq", allocationSize = 1)
    private Long id;

    @Column(unique=true, nullable=false)
    private String definedId;

    @NotNull
    private LocalDate created_date;

    @NotNull
    private LocalDate modified_date;

    @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      mappedBy = "staticPage")
    private List<StaticPageI18n> translations;

    /*% if (feature.UserManagement) { %*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private UMUser author;
    /*% } %*/

    public StaticPage(){

    }

    public StaticPage(
      Long id,
      String definedId,
      LocalDate created_date,
      LocalDate modified_date/*% if (feature.UserManagement) { %*/,
      UMUser author
      /*% } %*/
      ) {

      this.id = id;
      this.definedId = definedId;
      this.created_date = created_date;
      this.modified_date = modified_date;
      /*% if (feature.UserManagement) { %*/
      this.author = author;
      /*% } %*/
    }

    public Long getId(){
    	return id;
    }

    public void setId(Long id){
    	this.id = id;
    }

    public String getDefinedId() {
		return definedId;
	}

	public void setDefinedId(String definedId) {
		this.definedId = definedId;
	}

    public LocalDate getCreated_date(){
    	return created_date;
    }

    public void setCreated_date(LocalDate created_date){
    	this.created_date = created_date;
    }

    public LocalDate getModified_date(){
    	return modified_date;
    }

    public void setModified_date(LocalDate modified_date){
    	this.modified_date = modified_date;
    }

    public List<StaticPageI18n> getTranslations() {
      return translations;
    }

    public void setTranslations(List<StaticPageI18n> translations) {
      this.translations = translations;
    }

    /*% if (feature.UserManagement) { %*/
    public UMUser getAuthor(){
    	return author;
    }

    public void setAuthor(UMUser author){
    	this.author = author;
    }
    /*% } %*/
}
/*% } %*/
