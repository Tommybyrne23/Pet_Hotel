package com.tus.payment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.AmountWithBreakdown;
import com.paypal.sdk.models.CheckoutPaymentIntent;
import com.paypal.sdk.models.PaypalWalletExperienceContext;
import com.paypal.sdk.models.LinkDescription;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderRequest;
import com.paypal.sdk.models.CreateOrderInput;
import com.paypal.sdk.models.PaymentSource;
import com.paypal.sdk.models.PaypalWallet;
import com.paypal.sdk.models.PurchaseUnitRequest;

public class PaymentServices {
    private static final String CLIENT_ID = "BAAVMM_6WtNW3NIjHV1_Oo5pUffZU8Z3fnua4MqRzVqmqvBfW-FO3yDd5RNVv6kgo23t4qyrpbMJf2QQ_8";
    private static final String CLIENT_SECRET = "EKyK2cV6wUEewb01kYoPFRQ-5UADeHXtiUeEsMVini9Fekot5Dq17_2plAGaRLb_NVV6dVoHzS2zSSvP";
	private static final String MODE = "sandbox";
	
	private final PaypalServerSdkClient client;

    public PaymentServices() {
        // Initializes client; OAuth token management happens automatically
        this.client = new PaypalServerSdkClient.Builder()
                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(CLIENT_ID, CLIENT_SECRET).build())
                .environment(Environment.SANDBOX)
                .build();
    }

    public String authorizePayment(OrderDetail orderDetail, String baseUrl) throws ApiException, IOException {
        OrdersController ordersController = client.getOrdersController();

        // Build amount configuration, make sure amounts set to Eruro
        AmountWithBreakdown amount = new AmountWithBreakdown.Builder("EUR", orderDetail.getTotal()).build();

        // Build purchase unit item
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest.Builder(amount)
                .description(orderDetail.getProductName())
                .build();

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(purchaseUnit);

        // Configure dynamic redirect URLs for checkout experience
        PaypalWalletExperienceContext experienceContext = new PaypalWalletExperienceContext.Builder()
                .returnUrl(baseUrl + "/confirm.xhtml")	
                .cancelUrl(baseUrl + "/cancel.xhtml")
                .build();

        PaypalWallet paypalWallet = new PaypalWallet.Builder()
                .experienceContext(experienceContext)
                .build();

        PaymentSource paymentSource = new PaymentSource.Builder()
                .paypal(paypalWallet)
                .build();

        // Assemble order creation request
        OrderRequest orderRequest = new OrderRequest.Builder(CheckoutPaymentIntent.AUTHORIZE, purchaseUnits)
                .paymentSource(paymentSource)
                .build();

        CreateOrderInput createInput = new CreateOrderInput.Builder(null, orderRequest).build();

        // Execute API call
        ApiResponse<Order> response = ordersController.createOrder(createInput);
        Order order = response.getResult();

        // Extract approval redirect URL
        if (order != null && order.getLinks() != null) {
            for (LinkDescription link : order.getLinks()) {
                if ("payer-action".equalsIgnoreCase(link.getRel()) || "approve".equalsIgnoreCase(link.getRel())) {
                    return link.getHref();
                }
            }
        }
        return null;
    }
}
