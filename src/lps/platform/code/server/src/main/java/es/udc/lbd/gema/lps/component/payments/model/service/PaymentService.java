/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service;

import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
import es.udc.lbd.gema.lps.component.payments.exceptions.PaymentException;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentMethodType;
/*% if (feature.T_P_PayPal) { %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentDTO;
/*% } %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentFormDTO;
/*% if (feature.T_P_RedSys) { %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.RedSysDataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;

public interface PaymentService {

  /*% if (feature.T_P_RedSys) { %*/
  RedSysDataDTO redSysPayment(PaymentFormDTO paymentFormDto)
      throws ForbiddenUserException, NotFoundException, PaymentException;
  /*% } %*/
  /*% if (feature.T_P_PayPal) { %*/
  PaymentDTO payment(PaymentFormDTO paymentFormDto)
      throws ForbiddenUserException, NotFoundException, PaymentException;
  /*% } %*/
  /*% if (feature.T_P_RedSys) { %*/
  void captureOrderRedsys(String reference, RedSysDataDTO data)
      throws NotFoundException, PaymentException;
  /*% } %*/
  void captureOrder(String reference, PaymentMethodType type, String json)
      throws NotFoundException, PaymentException;
}
/*% } %*/
