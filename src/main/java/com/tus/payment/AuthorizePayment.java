package com.tus.payment;

import java.io.IOException;
import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import com.paypal.sdk.models.Order;

import com.paypal.sdk.exceptions.ApiException;


//BackingBean the payment pages bind to (#{authorizePayment.*}).
//Session-scoped so the call completes round-trip out to PayPal and back.

@Named
@SessionScoped
public class AuthorizePayment implements Serializable {
	private static final long serialVersionUID = 1L;
	private String paymentStatus;
	private String product;

	private float subTotal, shipping, tax, total;


		
	//getters and setters	
		
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public float getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(float subTotal) {
		this.subTotal = subTotal;
	}
	public float getShipping() {
		return shipping;
	}
	public void setShipping(float shipping) {
		this.shipping = shipping;
	}
	public float getTax() {
		return tax;
	}
	public void setTax(float tax) {
		this.tax = tax;
	}
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	
	//creates the order and passed on the information to Paypal to confirm the booking. 
	public String checkOut() throws IOException {
	    try {
	        jakarta.faces.context.ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
	        String baseUrl = extContext.getRequestScheme() + "://" + 
	                         extContext.getRequestServerName() + ":" + 
	                         extContext.getRequestServerPort() + 
	                         extContext.getRequestContextPath();

	        OrderDetail orderDetail = new OrderDetail(product, subTotal, shipping, tax, total);
	        PaymentServices paymentServices = new PaymentServices();
	        
	        String approvalLink = paymentServices.authorizePayment(orderDetail, baseUrl);
	        if (approvalLink != null) {
	            extContext.redirect(approvalLink);   
	        }
	    } catch (ApiException ex) {
	        ex.printStackTrace();
	    }
	    return null;
	}
		
	public String getPaymentStatus() {
	    return paymentStatus;
	}

	
	//Confirm runs when PayPal redirects back to confirm.xhtml. "Token" in the message is the orderID
	public void confirm() {
	    if (paymentStatus != null) {
	        return; // already captured — ignore page refreshes
	    }
	    jakarta.faces.context.ExternalContext ext =
	            FacesContext.getCurrentInstance().getExternalContext();
	    String orderId = ext.getRequestParameterMap().get("token");
	    if (orderId == null) {
	        paymentStatus = "No order token returned from PayPal.";
	        return;
	    }
	    try {
	        Order order = new PaymentServices().captureOrder(orderId);
	        paymentStatus = (order != null && order.getStatus() != null)
	                ? order.getStatus().toString()   // e.g. "COMPLETED"
	                : "UNKNOWN";
	    } catch (ApiException | IOException ex) {
	        ex.printStackTrace();
	        paymentStatus = "Payment could not be completed: " + ex.getMessage();
	    }
	}

}
