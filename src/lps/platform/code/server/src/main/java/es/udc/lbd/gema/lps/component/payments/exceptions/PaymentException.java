/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.exceptions;

import es.udc.lbd.gema.lps.model.service.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class PaymentException extends AppException {
  public PaymentException(String msg) {
    super("payment.error.payment_error", msg, HttpStatus.BAD_REQUEST);
  }
}
/*% } %*/
