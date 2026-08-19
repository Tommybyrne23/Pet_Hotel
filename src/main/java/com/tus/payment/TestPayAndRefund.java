package com.tus.payment;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestPayAndRefund {

    // sandbox app credentials 
	 private static final String CLIENT_ID = "BAAVMM_6WtNW3NIjHV1_Oo5pUffZU8Z3fnua4MqRzVqmqvBfW-FO3yDd5RNVv6kgo23t4qyrpbMJf2QQ_8";
	 private static final String CLIENT_SECRET = "EKyK2cV6wUEewb01kYoPFRQ-5UADeHXtiUeEsMVini9Fekot5Dq17_2plAGaRLb_NVV6dVoHzS2zSSvP";
    
	 
	 public static void main(String[] args) throws ApiException, IOException {

        PaypalServerSdkClient client = new PaypalServerSdkClient.Builder()
                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(
                        CLIENT_ID, CLIENT_SECRET).build())
                .environment(Environment.SANDBOX)
                .build();

        OrdersController orders = client.getOrdersController();

        // ---- Step 1: create a €10.00 order and get the approval link ----
        AmountWithBreakdown amount =
                new AmountWithBreakdown.Builder("EUR", "10.00").build();

        PurchaseUnitRequest purchaseUnit =
                new PurchaseUnitRequest.Builder(amount)
                        .description("Capture id test")
                        .build();

        List<PurchaseUnitRequest> units = new ArrayList<>();
        units.add(purchaseUnit);

        PaypalWalletExperienceContext ctx =
                new PaypalWalletExperienceContext.Builder()
                        .returnUrl("https://example.com/return")   // ignored - we capture by order id
                        .cancelUrl("https://example.com/cancel")
                        .build();

        PaymentSource source = new PaymentSource.Builder()
                .paypal(new PaypalWallet.Builder().experienceContext(ctx).build())
                .build();

        OrderRequest orderRequest =
                new OrderRequest.Builder(CheckoutPaymentIntent.CAPTURE, units)
                        .paymentSource(source)
                        .build();

        ApiResponse<Order> createResp =
                orders.createOrder(new CreateOrderInput.Builder(null, orderRequest).build());
        Order order = createResp.getResult();

        String orderId = order.getId();
        System.out.println("Order id: " + orderId);

        String approveUrl = null;
        if (order.getLinks() != null) {
            for (LinkDescription link : order.getLinks()) {
                if ("payer-action".equalsIgnoreCase(link.getRel())
                        || "approve".equalsIgnoreCase(link.getRel())) {
                    approveUrl = link.getHref();
                }
            }
        }

        System.out.println("\n1. Open this link and approve with your SANDBOX BUYER account:");
        System.out.println("   " + approveUrl);
        System.out.println("\n2. After you see the 'return' page, come back here and press Enter...");
        new Scanner(System.in).nextLine();

        // ---- Step 2: capture the approved order and dig out the capture id ----
        ApiResponse<Order> captureResp =
                orders.captureOrder(new CaptureOrderInput.Builder(orderId, null).build());
        Order captured = captureResp.getResult();

        System.out.println("\nCapture order status: " + captured.getStatus());   // COMPLETED
        System.out.println("Capture id:           " + extractCaptureId(captured));
    }

    // Same navigation you'll reuse in PaymentServices:
    // Order -> purchaseUnits -> payments -> captures -> id
    private static String extractCaptureId(Order order) {
        if (order == null || order.getPurchaseUnits() == null) {
            return null;
        }
        for (PurchaseUnit pu : order.getPurchaseUnits()) {
            if (pu.getPayments() != null && pu.getPayments().getCaptures() != null) {
                for (OrdersCapture capture : pu.getPayments().getCaptures()) {
                    return capture.getId();
                }
            }
        }
        return null;
    }
}