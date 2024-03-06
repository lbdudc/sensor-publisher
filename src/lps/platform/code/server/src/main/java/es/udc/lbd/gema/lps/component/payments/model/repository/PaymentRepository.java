/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.repository;

import es.udc.lbd.gema.lps.component.payments.model.domain.Payment;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository
    extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

  Optional<Payment> findByReference(String reference);
  /*% if (feature.T_P_RedSys) { %*/

  // XXX Sequence must be created on DB
  @Query(value = "select nextval('p_payment_reference_seq')", nativeQuery = true)
  BigDecimal getNextValReference();
  /*% } %*/
}
/*% } %*/
