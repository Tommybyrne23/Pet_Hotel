package com.tus.payment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.AmountWithBreakdown;
import com.paypal.sdk.models.CaptureOrderInput;
import com.paypal.sdk.models.CheckoutPaymentIntent;
import com.paypal.sdk.models.PaypalWalletExperienceContext;
import com.paypal.sdk.models.PurchaseUnit;
import com.paypal.sdk.models.LinkDescription;
import com.paypal.sdk.models.Order;
import com.paypal.sdk.models.OrderRequest;
import com.paypal.sdk.models.OrdersCapture;
import com.paypal.sdk.models.PaymentCollection;
import com.paypal.sdk.models.CreateOrderInput;
import com.paypal.sdk.models.PaymentSource;
import com.paypal.sdk.models.PaypalWallet;
import com.paypal.sdk.models.PurchaseUnitRequest;
import com.paypal.sdk.models.Refund;
import com.paypal.sdk.models.RefundCapturedPaymentInput;

import jakarta.faces.context.FacesContext;

import jakarta.enterprise.context.ApplicationScoped; // or @RequestScoped / @Dependent

@ApplicationScoped
public class PaymentServices { 

	//credentials taken from the developer portal signed up with A00347373
	private static final String CLIENT_ID = "BAAVMM_6WtNW3NIjHV1_Oo5pUffZU8Z3fnua4MqRzVqmqvBfW-FO3yDd5RNVv6kgo23t4qyrpbMJf2QQ_8";
	private static final String CLIENT_SECRET = "EKyK2cV6wUEewb01kYoPFRQ-5UADeHXtiUeEsMVini9Fekot5Dq17_2plAGaRLb_NVV6dVoHzS2zSSvP";

	//this may be legacy code from TUS week example, new environment.SANDBOX mode is imported  
	//private static final String MODE = "sandbox";

	private final PaypalServerSdkClient client;
	private String paymentStatus;			//used for returning Jarkata faces messages

	public PaymentServices() {
		// Initializes client; OAuth token management happens automatically
		this.client = new PaypalServerSdkClient.Builder()
				.clientCredentialsAuth(new ClientCredentialsAuthModel.Builder(CLIENT_ID, CLIENT_SECRET).build())
				.environment(Environment.SANDBOX)
				.build();
	}


	//Creates the order on Paypal and then returns the payment link to the app to redirect the user to complete the payment 
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

		// Configure dynamic redirect URLs for checkout experience. BaseURL allows for LocalHost:PORT dependencies for individual computers 

		PaypalWalletExperienceContext experienceContext = new PaypalWalletExperienceContext.Builder()
				.returnUrl(baseUrl + "/payments/confirm.xhtml")
				.cancelUrl(baseUrl + "/payments/cancel.xhtml")
				.build();

		PaypalWallet paypalWallet = new PaypalWallet.Builder()
				.experienceContext(experienceContext)
				.build();

		PaymentSource paymentSource = new PaymentSource.Builder()
				.paypal(paypalWallet)
				.build();

		// Assemble order creation request - use capture to allow for single 
		OrderRequest orderRequest = new OrderRequest.Builder(CheckoutPaymentIntent.CAPTURE, purchaseUnits)
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
	//method for processing Orders
	public Order captureOrder(String orderId) throws ApiException, IOException {
		OrdersController ordersController = client.getOrdersController();
		CaptureOrderInput captureInput = new CaptureOrderInput.Builder(orderId, null).build();
		ApiResponse<Order> response = ordersController.captureOrder(captureInput);
		return response.getResult();   // status becomes COMPLETED on success
	}

	
	/*
	 * API Call used to process a refund on PayPal using the CaptureID)
	 * template being adapted from captureOrder above
	 *	 https://github.com/paypal/PayPal-Java-Server-SDK/blob/main/src/main/java/com/paypal/sdk/models/RefundCapturedPaymentInput.java
	 */
	 
	
	public Refund refundCapture(String captureID) throws ApiException, IOException{
		PaymentsController paymentsController = client.getPaymentsController();
		RefundCapturedPaymentInput captureRefund = new RefundCapturedPaymentInput.Builder().captureId(captureID).build();
		ApiResponse<Refund> response = paymentsController.refundCapturedPayment(captureRefund);
		return response.getResult(); //refund 
	}


	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void confirm() {
		// Guard: don't re-capture if the page is refreshed (PayPal rejects a second capture)
		if (paymentStatus != null) {
			return;
		}
		jakarta.faces.context.ExternalContext ext =
				FacesContext.getCurrentInstance().getExternalContext();
		String orderId = ext.getRequestParameterMap().get("token");
		if (orderId == null) {
			paymentStatus = "No order confirmation has been returned from PayPal.";			//change the message to remove the word token for everyday users. 
			return;
		}
		try {
			Order order = new PaymentServices().captureOrder(orderId);
			paymentStatus = (order != null && order.getStatus() != null)
					? order.getStatus().toString()      // e.g. "COMPLETED"
							: "UNKNOWN";
		} catch (ApiException | IOException ex) {
			ex.printStackTrace();
			paymentStatus = "Payment could not be completed: " + ex.getMessage();
		}
	}

	/*
	 * This method takes the order ID of a successful payment and uses it to extract 
	 * the capture ID of a successful Paypal Payment. This capture ID is then stored in 
	 * reservations to be used for the refund process. 
	 */

	public static String extractCaptureID(Order order) {
		//there is no order information return null. 
		if (order == null) {
			return null;
		}
		
		//if there are no purchase units (eitehr returns null or EMPTY on the order returns a null value. 
		List<PurchaseUnit> purchaseUnits = order.getPurchaseUnits();
		if (purchaseUnits == null || purchaseUnits.isEmpty()) {
			return null; 
		}
		
		//if no payments were captured, again return a null value
		PaymentCollection payments = purchaseUnits.get(0).getPayments();
		if (payments == null) {
			return null;
		}


		List<OrdersCapture> captures = payments.getCaptures();
		//If no payments were captured return null
		if (captures == null || captures.isEmpty()) return null;          
		
		//otherwise return the Paypal Capture ID from this item and use it in the BookingBean to write to the reservation. 
		return captures.get(0).getId();                                  
	}

}


