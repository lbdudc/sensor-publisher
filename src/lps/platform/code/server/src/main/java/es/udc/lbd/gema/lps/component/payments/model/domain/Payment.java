/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "p_payment")
@Table(name = "p_payment")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_paymentid")
  @SequenceGenerator(
      name = "p_paymentid",
      sequenceName = "p_payment_id_seq",
      initialValue = 1,
      allocationSize = 1)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private PaymentStatus status;

  @Column(name = "amount")
  private Double amount;

  @Column(name = "date_created")
  private LocalDateTime dateCreated;

  @Column(name = "reference")
  private String reference;

  @Column(name = "refund")
  private Boolean refund;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private PaymentMethodType type;

  // TODO add reference to um_user (?)

  public Payment() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(LocalDateTime dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public Boolean getRefund() {
    return refund;
  }

  public void setRefund(Boolean refund) {
    this.refund = refund;
  }

  public PaymentMethodType getType() {
    return type;
  }

  public void setType(PaymentMethodType type) {
    this.type = type;
  }
}
/*% } %*/
