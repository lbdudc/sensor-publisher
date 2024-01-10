/*% if (feature.T_P_PayPal) { %*/
package es.udc.lbd.gema.lps.component.payments.model.service.strategy;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.AmountBreakdown;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Item;
import com.paypal.orders.Money;
import com.paypal.orders.Name;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.OrdersGetRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.orders.ShippingDetail;
import es.udc.lbd.gema.lps.component.payments.exceptions.PaymentException;
import es.udc.lbd.gema.lps.component.payments.model.domain.Payment;
import es.udc.lbd.gema.lps.component.payments.model.domain.PaymentStatus;
import es.udc.lbd.gema.lps.component.payments.model.service.dto.PaymentDTO;
import es.udc.lbd.gema.lps.config.PaypalConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaypalPayment implements PaymentStrategy {

  private String clientId;
  private String secret;
  private PayPalHttpClient client;

  @Autowired
  public PaypalPayment(PaypalConfiguration paypalConfiguration) {
    this.clientId = paypalConfiguration.getClientId();
    this.secret = paypalConfiguration.getSecret();
    configureClient();
  }

  private void configureClient() {
    PayPalEnvironment environment = new PayPalEnvironment.Sandbox(clientId, secret);
    this.client = new PayPalHttpClient(environment);
  }

  @Override
  public PaymentDTO createOrder(Payment payment, String fullName)
      throws IOException, PaymentException {
    OrdersCreateRequest request = new OrdersCreateRequest();
    request.prefer("return=representation");
    request.requestBody(buildCreateOrder(payment.getAmount(), fullName));
    // 3. Call PayPal to set up a transaction
    HttpResponse<Order> response = client.execute(request);

    if (response.statusCode() != 201) throw new PaymentException("Error creating order");

    payment.setStatus(PaymentStatus.PENDING);
    payment.setReference(response.result().id());
    return new PaymentDTO(payment);
  }

  private OrderRequest buildCreateOrder(Double amount, String fullName) {
    OrderRequest orderRequest = new OrderRequest();
    orderRequest.checkoutPaymentIntent("CAPTURE");

    ApplicationContext applicationContext =
        new ApplicationContext()
            .brandName("LEMSI")
            .landingPage("NO_PREFERENCE")
            .shippingPreference("NO_SHIPPING");
    orderRequest.applicationContext(applicationContext);

    List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<PurchaseUnitRequest>();
    ArrayList<Item> items = new ArrayList<Item>();
    items.add(
        new Item()
            .name("Carga monedero")
            .description("Recarga de monedero")
            .sku("sku01")
            .unitAmount(new Money().currencyCode("EUR").value(amount.toString()))
            .quantity("1")
            .category("DIGITAL_GOODS"));
    PurchaseUnitRequest purchaseUnitRequest =
        new PurchaseUnitRequest()
            .description("Recarga de monedero")
            .customId("Id interno de la recarga")
            .softDescriptor("LemsiPay")
            .amountWithBreakdown(
                new AmountWithBreakdown()
                    .currencyCode("EUR")
                    .value(amount.toString())
                    .amountBreakdown(
                        new AmountBreakdown()
                            .itemTotal(new Money().currencyCode("EUR").value(amount.toString()))))
            .items(items)
            .shippingDetail(new ShippingDetail().name(new Name().fullName(fullName)));
    purchaseUnitRequests.add(purchaseUnitRequest);
    orderRequest.purchaseUnits(purchaseUnitRequests);
    return orderRequest;
  }

  @Override
  public void captureOrder(Payment payment, String json) throws IOException, PaymentException {
    OrdersGetRequest transactionRequest = new OrdersGetRequest(payment.getReference());
    HttpResponse<Order> transactionResponse = this.client.execute(transactionRequest);
    if (transactionResponse.statusCode() != 200)
      throw new PaymentException(
          "Unable to find PayPal transaction with id " + payment.getReference());
    Double orderAmount =
        Double.parseDouble(
            transactionResponse.result().purchaseUnits().get(0).amountWithBreakdown().value());
    if (!orderAmount.equals(payment.getAmount()))
      throw new PaymentException("Invalid amount for transaction");

    OrdersCaptureRequest request = new OrdersCaptureRequest(payment.getReference());
    request.requestBody(new OrderRequest());
    // Call Paypal
    HttpResponse<Order> response = this.client.execute(request);
    if (response.statusCode() != 201)
      throw new PaymentException(
          "Unable to capture Paypal transaction with id " + payment.getReference());
    payment.setStatus(PaymentStatus.CONFIRMED);
  }
}
/*% } %*/
