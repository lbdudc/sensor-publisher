/*% if (feature.DM_DS_GTFS) { %*/
package es.udc.lbd.gema.lps.component.gtfs.model.service.dto;

import es.udc.lbd.gema.lps.component.gtfs.model.domain.Agency;

public class AgencyDTO {

  private String agencyId;
  private String agencyName;
  private String agencyUrl;
  private String agencyTimezone;
  private String agencyLang;
  private String agencyPhone;
  private String agencyFareUrl;
  private String agencyEmail;

  public AgencyDTO() {}

  public AgencyDTO(Agency agency) {
    this.agencyId = agency.getAgencyId();
    this.agencyName = agency.getAgencyName();
    this.agencyUrl = agency.getAgencyUrl();
    this.agencyTimezone = agency.getAgencyTimezone();
    this.agencyLang = agency.getAgencyLang();
    this.agencyPhone = agency.getAgencyPhone();
    this.agencyFareUrl = agency.getAgencyFareUrl();
    this.agencyEmail = agency.getAgencyEmail();
  }

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

  public Agency toAgency() {
    Agency agency = new Agency();
    agency.setAgencyId(this.getAgencyId());
    agency.setAgencyName(this.getAgencyName());
    agency.setAgencyUrl(this.getAgencyUrl());
    agency.setAgencyTimezone(this.getAgencyTimezone());
    agency.setAgencyLang(this.getAgencyLang());
    agency.setAgencyPhone(this.getAgencyPhone());
    agency.setAgencyFareUrl(this.getAgencyFareUrl());
    agency.setAgencyEmail(this.getAgencyEmail());
    return agency;
  }
}
/*% } %*/
