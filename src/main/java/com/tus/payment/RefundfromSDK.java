/* Code taken from 
 * https://developer.paypal.com/serversdk/java/api-endpoints/payments/refund-captured-payment
 * 
 * */

package com.tus.payment;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.Refund;
import com.paypal.sdk.models.RefundCapturedPaymentInput;

import java.io.IOException;



public class RefundfromSDK {

	
    public static void main(String[] args) throws ApiException, IOException {
        PaypalServerSdkClient client = new PaypalServerSdkClient.Builder()
                .clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(
                		"BAAVMM_6WtNW3NIjHV1_Oo5pUffZU8Z3fnua4MqRzVqmqvBfW-FO3yDd5RNVv6kgo23t4qyrpbMJf2QQ_8",				// client ID --> to be moved to an envornment variable
                        "EKyK2cV6wUEewb01kYoPFRQ-5UADeHXtiUeEsMVini9Fekot5Dq17_2plAGaRLb_NVV6dVoHzS2zSSvP")					//secret should move to an environmnent file really
                        .build())
                .environment(Environment.SANDBOX)
                .build();

        PaymentsController paymentsController = client.getPaymentsController();

        // captureId comes from a real captured sandbox payment (see below).
        // Second arg is contentType - null is fine; set "application/json" if it complains.
        // No .body(...) = refund the FULL captured amount.
        RefundCapturedPaymentInput input =
                new RefundCapturedPaymentInput.Builder("3F182647J4050760U", null).build();

        ApiResponse<Refund> response = paymentsController.refundCapturedPayment(input);
        Refund refund = response.getResult();

        System.out.println("Refund id:     " + refund.getId());
        System.out.println("Refund status: " + refund.getStatus());   // COMPLETED / PENDING
    }
}