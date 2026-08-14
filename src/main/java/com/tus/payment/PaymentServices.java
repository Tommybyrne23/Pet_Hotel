package com.tus.payment;

public class PaymentServices {

	private static final String CLIENT_ID="BAAVMM_6WtNW3NIjHV1_Oo5pUffZU8Z3fnua4MqRzVqmqvBfW-FO3yDd5RNVv6kgo23t4qyrpbMJf2QQ_8";
	private static final String CLIENT_SECRET="EKyK2cV6wUEewb01kYoPFRQ-5UADeHXtiUeEsMVini9Fekot5Dq17_2plAGaRLb_NVV6dVoHzS2zSSvP";
	private static final String MODE = "sandbox";
	
	private Payer get payerInformation() {
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		
		PayerInfo payerInfo = new PayerInfo();
		payerInfo.setFirstName("John").setLastName("Doe").setEmail("sb-bvh5l52480718@personal.example.com");
		
		payer.SetPayerInfo(payerInfo);
		
		
		
		return;
	}
	
	
}
