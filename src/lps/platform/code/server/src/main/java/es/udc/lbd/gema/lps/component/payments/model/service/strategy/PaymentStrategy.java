/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.strategy;

import es.udc.lbd.gema.lps.component.payments.exceptions.PaymentException;
import es.udc.lbd.gema.lps.component.payments.model.domain.Payment;
import java.io.IOException;

public interface PaymentStrategy {
  public Object createOrder(Payment payment, String fullName) throws IOException, PaymentException;

  public void captureOrder(Payment payment, String json) throws IOException, PaymentException;
}
/*% } %*/
