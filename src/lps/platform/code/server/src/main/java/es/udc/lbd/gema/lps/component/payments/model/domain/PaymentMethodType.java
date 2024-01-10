/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.domain;

public enum PaymentMethodType {
  /*% if (feature.T_P_RedSys) { %*/CREDIT_CARD,/*% } %*/
  /*% if (feature.T_P_PayPal) { %*/PAYPAL,/*% } %*/
}
/*% } %*/
