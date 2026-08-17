package com.tus.payment;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productName;
    private float subtotal;
    private float shipping;
    private float tax;
    private float total;

    public OrderDetail() {
    }

    public OrderDetail(String productName, float subtotal,
            float shipping, float tax, float total) {
        this.productName = productName;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.tax = tax;
        this.total = total;
    }

    public String getProductName() {
        return productName;
    }

    public String getSubtotal() {
        return String.format("%.2f", subtotal);
    }

    public String getShipping() {
        return String.format("%.2f", shipping);
    }

    public String getTax() {
        return String.format("%.2f", tax);
    }

    
    //use Locale.ROOT (will use dot.decimal in Ireland. France/Germany would return a com,a  
    public String getTotal() {
        return String.format(java.util.Locale.ROOT, "%.2f", total);
    }
}
