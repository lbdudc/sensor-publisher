/*% if (feature.T_P_RedSys) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RedSysDataDTO {

  private String errorCode;

  @JsonProperty("Ds_SignatureVersion")
  private String Ds_SignatureVersion;

  @JsonProperty("Ds_Signature")
  private String Ds_Signature;

  @JsonProperty("Ds_MerchantParameters")
  private String Ds_MerchantParameters;

  public RedSysDataDTO() {}

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getDs_SignatureVersion() {
    return Ds_SignatureVersion;
  }

  public void setDs_SignatureVersion(String ds_SignatureVersion) {
    Ds_SignatureVersion = ds_SignatureVersion;
  }

  public String getDs_Signature() {
    return Ds_Signature;
  }

  public void setDs_Signature(String ds_Signature) {
    Ds_Signature = ds_Signature;
  }

  public String getDs_MerchantParameters() {
    return Ds_MerchantParameters;
  }

  public void setDs_MerchantParameters(String ds_MerchantParameters) {
    Ds_MerchantParameters = ds_MerchantParameters;
  }
}
/*% } %*/
