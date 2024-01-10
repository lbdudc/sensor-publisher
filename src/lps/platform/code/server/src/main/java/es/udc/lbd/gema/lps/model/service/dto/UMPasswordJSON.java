/*% if (feature.UM_UP_ByUser) { %*/
package es.udc.lbd.gema.lps.model.service.dto;

public class UMPasswordJSON {

  private String password;

  public UMPasswordJSON() {}

  public UMPasswordJSON(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }
}
/*% } %*/