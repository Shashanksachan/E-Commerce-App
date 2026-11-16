package com.example.inventoryservice.event;

import java.util.UUID;

public class InventoryReservedEvent {

    private Long orderId;
    private String productName;
    private Integer quantity;
    private String eventId = UUID.randomUUID().toString();

    public InventoryReservedEvent() {
    }

    public InventoryReservedEvent(Long orderId,
                                  String productName,
                                  Integer quantity) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}