/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.strategy;

import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentMethodType;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

@Component
public class PaymentStrategyFactory {

  /*% if (feature.T_P_PayPal) { %*/
  @Inject private PaypalPayment paypalPayment;
  /*% } %*/
  /*% if (feature.T_P_RedSys) { %*/
  @Inject private CreditCardPayment creditCardPayment;
  /*% } %*/

  public PaymentStrategy createStrategy(PaymentMethodType paymentMethodType) {
    switch (paymentMethodType) {
      /*% if (feature.T_P_PayPal) { %*/
      case PAYPAL:
        return paypalPayment;
      /*% } %*/
      /*% if (feature.T_P_RedSys) { %*/
      case CREDIT_CARD:
        return creditCardPayment;
      /*% } %*/
      default:
        return null;
    }
  }
}
/*% } %*/
