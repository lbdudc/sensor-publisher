/*% if (feature.T_P_RedSys) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.lbd.gema.lps.component.payments.exceptions.PaymentException;
import es.udc.lbd.gema.lps.component.payments.model.domain.Payment;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentStatus;
import es.udc.lbd.gema.lps.component.payments.model.repository.PaymentRepository;
import es.udc.lbd.gema.lps.component.payments.model.service.dto.RedSysDataDTO;
import es.udc.lbd.gema.lps.config.RedSysConfiguration;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sis.redsys.api.ApiMacSha256;

@Component
public class CreditCardPayment implements PaymentStrategy {

  @Inject private PaymentRepository paymentRepository;
  private String currency;
  private String key;
  private String merchantCode;
  private String publicClientUrl;
  private String publicServerUrl;
  private String terminal;
  private ApiMacSha256 apiMacSha256;

  @Autowired
  public CreditCardPayment(RedSysConfiguration redSysConfiguration) {
    this.currency = redSysConfiguration.getCurrency();
    this.key = redSysConfiguration.getKey();
    this.merchantCode = redSysConfiguration.getMerchantCode();
    this.publicClientUrl = redSysConfiguration.getPublicClientUrl();
    this.publicServerUrl = redSysConfiguration.getPublicServerUrl();
    this.terminal = redSysConfiguration.getTerminal();
    this.apiMacSha256 = new ApiMacSha256();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RedSysDataDTO createOrder(Payment payment, String fullName)
      throws IOException, PaymentException {
    payment.setStatus(PaymentStatus.PENDING);
    // Generate reference with DB sequence for RedSys payment
    BigDecimal reference = paymentRepository.getNextValReference();
    payment.setReference(reference.toPlainString());
    RedSysDataDTO redSysData = new RedSysDataDTO();
    String amount;
    DecimalFormat df2 = new DecimalFormat("#.##");
    if (payment.getAmount() % 1 == 0) {
      amount = payment.getAmount().intValue() + "00";
    } else {
      amount =
          BigDecimal.valueOf(payment.getAmount())
              .setScale(2, RoundingMode.HALF_UP)
              .toString()
              .replace(".", "");
    }
    apiMacSha256.setParameter("DS_MERCHANT_AMOUNT", stringToUTF8(amount));
    apiMacSha256.setParameter("DS_MERCHANT_CURRENCY", stringToUTF8(currency));
    apiMacSha256.setParameter("DS_MERCHANT_MERCHANTCODE", stringToUTF8(merchantCode));
    apiMacSha256.setParameter(
        "DS_MERCHANT_MERCHANTURL",
        stringToUTF8(
            publicServerUrl + "/payments/capture-order/redsys/" + payment.getReference()));
    apiMacSha256.setParameter("DS_MERCHANT_TERMINAL", stringToUTF8(terminal));
    apiMacSha256.setParameter("DS_MERCHANT_TRANSACTIONTYPE", stringToUTF8("0"));
    apiMacSha256.setParameter("DS_MERCHANT_ORDER", stringToUTF8(payment.getReference()));
    apiMacSha256.setParameter(
        "DS_MERCHANT_MERCHANTURL",
        stringToUTF8(
            publicServerUrl + "/payments/capture-order/redsys/" + payment.getReference()));
    apiMacSha256.setParameter(
        "DS_MERCHANT_URLKO",
        stringToUTF8(publicClientUrl + "/payments/tpv-ko?reference=" + payment.getReference()));
    apiMacSha256.setParameter(
        "DS_MERCHANT_URLOK",
        stringToUTF8(publicClientUrl + "/payments/tpv-ok?reference=" + payment.getReference()));
    redSysData.setDs_MerchantParameters(apiMacSha256.createMerchantParameters());
    redSysData.setDs_SignatureVersion("HMAC_SHA256_V1");
    try {
      redSysData.setDs_Signature(apiMacSha256.createMerchantSignature(key));
    } catch (InvalidKeyException
        | NoSuchAlgorithmException
        | IllegalStateException
        | NoSuchPaddingException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new PaymentException("Unable to create DS_Signature");
    }
    return redSysData;
  }

  private String stringToUTF8(String string) {
    return new String(string.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
  }

  @Override
  public void captureOrder(Payment payment, String json) throws IOException, PaymentException {
    // Parse request body to redSysData object
    ObjectMapper mapper = new ObjectMapper();
    RedSysDataDTO redSysData = mapper.readValue(json, RedSysDataDTO.class);
    try {
      if (redSysData.getErrorCode() != null) {
        throw new PaymentException("Transaction not confirmed from RedSys");
      }
      // Decode order parameters
      apiMacSha256.decodeMerchantParameters(redSysData.getDs_MerchantParameters());
      // Check response code between 0000 and 0099
      String response = apiMacSha256.getParameter("Ds_Response");
      if (Integer.parseInt(response) < 0 || Integer.parseInt(response) > 99) {
        throw new PaymentException("Operation returned error code: " + response);
      }
      // Signature check
      String expectedSignature =
          apiMacSha256.createMerchantSignatureNotif(key, redSysData.getDs_MerchantParameters());
      if (expectedSignature.equals(redSysData.getDs_Signature())) {
        payment.setStatus(PaymentStatus.CONFIRMED);
      } else {
        throw new PaymentException("DS_Signature not valid");
      }
    } catch (InvalidKeyException
        | UnsupportedEncodingException
        | NoSuchAlgorithmException
        | IllegalStateException
        | NoSuchPaddingException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new PaymentException("Error creating RedSys signature");
    }
  }
}
/*% } %*/
