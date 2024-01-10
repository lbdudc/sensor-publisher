/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.domain;

import com.fasterxml.jackson.annotation.JsonView;
import es.udc.lbd.gema.lps.model.views.Views;
import javax.persistence.*;
import javax.persistence.Column;

@Entity(name = "t_agency")
@Table(name = "t_agency")
public class Agency {
  @Id
  @JsonView(Views.General.class)
  @Column(name = "agency_id", unique = true)
  private String agencyId;

  @JsonView(Views.General.class)
  @Column(name = "agency_name")
  private String agencyName;

  @JsonView(Views.General.class)
  @Column(name = "agency_url")
  private String agencyUrl;

  @JsonView(Views.General.class)
  @Column(name = "agency_timezone")
  private String agencyTimezone;

  @JsonView(Views.General.class)
  @Column(name = "agency_lang")
  private String agencyLang;

  @JsonView(Views.General.class)
  @Column(name = "agency_phone")
  private String agencyPhone;

  @JsonView(Views.General.class)
  @Column(name = "agency_fare_url")
  private String agencyFareUrl;

  @JsonView(Views.General.class)
  @Column(name = "agency_email")
  private String agencyEmail;

  public Agency() {}

  public String getAgencyId() {
    return agencyId;
  }

  public void setAgencyId(String agencyId) {
    this.agencyId = agencyId;
  }

  public String getAgencyName() {
    return agencyName;
  }

  public void setAgencyName(String agencyName) {
    this.agencyName = agencyName;
  }

  public String getAgencyUrl() {
    return agencyUrl;
  }

  public void setAgencyUrl(String agencyUrl) {
    this.agencyUrl = agencyUrl;
  }

  public String getAgencyTimezone() {
    return agencyTimezone;
  }

  public void setAgencyTimezone(String agencyTimezone) {
    this.agencyTimezone = agencyTimezone;
  }

  public String getAgencyLang() {
    return agencyLang;
  }

  public void setAgencyLang(String agencyLang) {
    this.agencyLang = agencyLang;
  }

  public String getAgencyPhone() {
    return agencyPhone;
  }

  public void setAgencyPhone(String agencyPhone) {
    this.agencyPhone = agencyPhone;
  }

  public String getAgencyFareUrl() {
    return agencyFareUrl;
  }

  public void setAgencyFareUrl(String agencyFareUrl) {
    this.agencyFareUrl = agencyFareUrl;
  }

  public String getAgencyEmail() {
    return agencyEmail;
  }

  public void setAgencyEmail(String agencyEmail) {
    this.agencyEmail = agencyEmail;
  }
}
/*% } %*/
