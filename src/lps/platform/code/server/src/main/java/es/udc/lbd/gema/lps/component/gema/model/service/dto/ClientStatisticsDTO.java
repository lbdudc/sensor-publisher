/*% if (feature.MWM_TE_VisitsRecord) { %*/
package es.udc.lbd.gema.lps.component.gema.model.service.dto;

import es.udc.lbd.gema.lps.component.gema.model.domain.Client;

public class ClientStatisticsDTO {

  private ClientDTO client;
  private Long visitsNumber;
  /*% if (feature.MVM_VT_WithTime) { %*/
  private Long plannedTime;
  /*% } %*/

  public ClientStatisticsDTO() {}

  public ClientStatisticsDTO(ClientDTO client, Long visitsNumber/*% if (feature.MVM_VT_WithTime) { %*/, Long plannedTime/*% } %*/) {
    this.client = client;
    this.visitsNumber = visitsNumber;
    /*% if (feature.MVM_VT_WithTime) { %*/
    this.plannedTime = plannedTime;
    /*% } %*/
  }

  public ClientStatisticsDTO(Client client, Long visitsNumber/*% if (feature.MVM_VT_WithTime) { %*/, Long plannedTime/*% } %*/) {
    this.client = new ClientDTO(client);
    this.visitsNumber = visitsNumber;
    /*% if (feature.MVM_VT_WithTime) { %*/
    this.plannedTime = plannedTime;
    /*% } %*/
  }

  public ClientDTO getClient() {
    return client;
  }

  public void setClient(ClientDTO client) {
    this.client = client;
  }

  public Long getVisitsNumber() {
    return visitsNumber;
  }

  public void setVisitsNumber(Long visitsNumber) {
    this.visitsNumber = visitsNumber;
  }
  /*% if (feature.MVM_VT_WithTime) { %*/
  public Long getPlannedTime() {
    return plannedTime;
  }

  public void setPlannedTime(Long plannedTime) {
    this.plannedTime = plannedTime;
  }
  /*% } %*/
}
/*% } %*/