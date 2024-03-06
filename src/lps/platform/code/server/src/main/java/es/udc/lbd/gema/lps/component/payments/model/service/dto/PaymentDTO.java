/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.dto;

import es.udc.lbd.gema.lps.component.payments.model.domain.Payment;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentMethodType;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentStatus;

public class PaymentDTO {
  private Long id;
  private Double amount;
  private PaymentStatus status;
  private PaymentMethodType type;
  private String reference;

  public PaymentDTO() {}

  public PaymentDTO(Payment payment) {
    this.id = payment.getId();
    this.amount = payment.getAmount();
    this.status = payment.getStatus();
    this.type = payment.getType();
    this.reference = payment.getReference();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public PaymentMethodType getType() {
    return type;
  }

  public void setType(PaymentMethodType type) {
    this.type = type;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }
}
/*% } %*/
