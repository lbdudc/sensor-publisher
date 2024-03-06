/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.dto;

import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentMethodType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PaymentFormDTO {

  @Max(value = 500)
  @Min(value = 5)
  private Double amount;

  private String fullName;

  private PaymentMethodType type;

  public PaymentFormDTO() {}

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public PaymentMethodType getType() {
    return type;
  }

  public void setType(PaymentMethodType type) {
    this.type = type;
  }
}
/*% } %*/
