package com.example.orderservice.event;

public class PaymentCompletedEvent {

    private Long orderId;
    private String paymentStatus;

    public PaymentCompletedEvent() {}

    public PaymentCompletedEvent(Long orderId, String paymentStatus) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}