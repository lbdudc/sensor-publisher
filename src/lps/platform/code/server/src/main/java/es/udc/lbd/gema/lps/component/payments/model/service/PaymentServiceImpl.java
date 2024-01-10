/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
import es.udc.lbd.gema.lps.component.payments.exceptions.PaymentException;
import es.udc.lbd.gema.lps.component.payments.model.domain.Payment;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentMethodType;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentStatus;
import es.udc.lbd.gema.lps.component.payments.model.repository.PaymentRepository;
/*% if (feature.T_P_PayPal) { %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentDTO;
/*% } %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentFormDTO;
/*% if (feature.T_P_RedSys) { %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.RedSysDataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.component.payments.model.service.strategy.PaymentStrategy;
import es.udc.lbd.gema.lps.component.payments.model.service.strategy.PaymentStrategyFactory;
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class PaymentServiceImpl implements PaymentService {

  @Inject private PaymentStrategyFactory paymentStrategyFactory;
  @Inject private PaymentRepository paymentRepository;

  /*% if (feature.T_P_RedSys) { %*/
  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public RedSysDataDTO redSysPayment(PaymentFormDTO paymentFormDto)
      throws ForbiddenUserException, NotFoundException, PaymentException {
    // Return RedSys strategy process result as RedSysDataDTO
    return (RedSysDataDTO) paymentCommon(paymentFormDto);
  }

  /*% } %*/
  /*% if (feature.T_P_PayPal) { %*/
  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public PaymentDTO payment(PaymentFormDTO paymentFormDto)
      throws ForbiddenUserException, NotFoundException, PaymentException {
    // Return PayPal strategy process result as wallet charge
    return (PaymentDTO) paymentCommon(paymentFormDto);
  }

  /*% } %*/
  protected Object paymentCommon(PaymentFormDTO paymentFormDto)
      throws ForbiddenUserException, NotFoundException, PaymentException {

    /*
     * Creating payment
     */
    Payment payment = new Payment();
    payment.setAmount(paymentFormDto.getAmount());
    payment.setType(paymentFormDto.getType());
    payment.setDateCreated(LocalDateTime.now(ZoneId.of("UTC")));
    /*
     * Execute payment following payment method strategy and update balance if
     * needed
     */
    PaymentStrategy paymentStrategy = paymentStrategyFactory.createStrategy(payment.getType());
    /*
     * Return strategy process result
     */

    try {
      Object result = paymentStrategy.createOrder(payment, paymentFormDto.getFullName());
      paymentRepository.save(payment);
      return result;
    } catch (Exception e) {
      throw new PaymentException("Error processing order");
    }
  }
  /*% if (feature.T_P_RedSys) { %*/

  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void captureOrderRedsys(String reference, RedSysDataDTO data)
      throws NotFoundException, PaymentException {
    try {
      String json = (new ObjectMapper()).writeValueAsString(data);
      captureOrderCommon(reference, PaymentMethodType.CREDIT_CARD, json);
    } catch (JsonProcessingException e) {
      throw new PaymentException("Unable to parse object to JSON");
    }
  }

  /*% } %*/
  @Override
  @Transactional(readOnly = false, rollbackFor = Exception.class)
  public void captureOrder(String reference, PaymentMethodType type, String json)
      throws NotFoundException, PaymentException {
    captureOrderCommon(reference, type, json);
  }

  protected void captureOrderCommon(String reference, PaymentMethodType type, String json)
      throws NotFoundException, PaymentException {
    Payment payment =
        paymentRepository.findByReference(reference).orElseThrow(() -> new NotFoundException("Payment with reference " + reference + " not found"));

    // If already confirmed transaction, do nothing
    if (payment.getStatus().equals(PaymentStatus.CONFIRMED)) {
      return;
    }

    // Capture order
    PaymentStrategy paymentStrategy = paymentStrategyFactory.createStrategy(type);
    try {
      paymentStrategy.captureOrder(payment, json);
    } catch (Exception e) {
      throw new PaymentException(e.getMessage());
    }
  }
}
/*% } %*/
