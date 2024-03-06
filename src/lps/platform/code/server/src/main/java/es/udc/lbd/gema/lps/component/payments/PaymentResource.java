/*% if (feature.T_Payments) { %*/
package es.udc.lbd.gema.lps.component.payments;

import es.udc.lbd.gema.lps.component.gema.model.service.exception.ForbiddenUserException;
import es.udc.lbd.gema.lps.component.payments.exceptions.PaymentException;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentMethodType;
import es.udc.lbd.gema.lps.component.payments.model.service.PaymentService;
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentDTO;
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentFormDTO;
/*% if (feature.T_P_RedSys) { %*/
import es.udc.lbd.gema.lps.component.payments.model.service.dto.RedSysDataDTO;
/*% } %*/
import es.udc.lbd.gema.lps.model.service.exceptions.NotFoundException;
import es.udc.lbd.gema.lps.web.rest.custom.ValidationErrorUtils;
import javax.inject.Inject;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
/*% if (feature.T_P_RedSys) { %*/
import org.springframework.http.MediaType;
/*% } %*/
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PaymentResource.PAYMENT_RESOURCE_URL)
public class PaymentResource {

  public static final String PAYMENT_RESOURCE_URL = "/api/payments";
  private static final Logger logger = LoggerFactory.getLogger(PaymentResource.class);

  @Inject private PaymentService paymentService;
  /*% if (feature.T_P_RedSys) { %*/

  @PostMapping("/redsys")
  public ResponseEntity<?> redSysPayment(@RequestBody PaymentFormDTO paymentFormDTO, Errors errors)
      throws NotFoundException, ForbiddenUserException, PaymentException {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }

    RedSysDataDTO result = paymentService.redSysPayment(paymentFormDTO);
    return ResponseEntity.ok().body(result);
  }

  /*% } %*/
  /*% if (feature.T_P_PayPal) { %*/
  @PostMapping
  public ResponseEntity<?> payment(@RequestBody @Valid PaymentFormDTO paymentFormDTO, Errors errors)
      throws NotFoundException, ForbiddenUserException, PaymentException {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorUtils.getValidationErrors(errors));
    }

    PaymentDTO result = paymentService.payment(paymentFormDTO);
    return ResponseEntity.ok().body(result);
  }

  /*% } %*/
  /*% if (feature.T_P_RedSys) { %*/
  @PostMapping(
      value = "capture-order/redsys/{reference}",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public ResponseEntity<?> captureOrder(@PathVariable String reference, RedSysDataDTO data)
      throws NotFoundException, PaymentException {

    paymentService.captureOrderRedsys(reference, data);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /*% } %*/
  @PostMapping("capture-order/{reference}")
  public ResponseEntity<?> captureOrder(
      @PathVariable String reference,
      @RequestParam(value = "type", required = false) PaymentMethodType type,
      @RequestBody(required = false) String json)
      throws NotFoundException, PaymentException {

    paymentService.captureOrder(reference, type, json);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
/*% } %*/
