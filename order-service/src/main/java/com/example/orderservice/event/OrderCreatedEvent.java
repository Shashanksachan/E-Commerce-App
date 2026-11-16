package com.example.orderservice.event;

import java.math.BigDecimal;

public class OrderCreatedEvent {

    private String eventId;
    private Long orderId;
    private String productName;
    private Integer quantity;
    private Double price;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(String eventId,
                             Long orderId,
                             String productName,
                             Integer quantity,
                             Double price) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}