package com.example.inventoryservice.consumer;

import com.example.inventoryservice.event.OrderCreatedEvent;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryService inventoryService;

    public InventoryConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "inventory-group"
    )
    public void consume(OrderCreatedEvent event) {

        System.out.println("========================");

        System.out.println("Order Received");

        System.out.println(event.getOrderId());

        System.out.println(event.getProductName());

        System.out.println(event.getQuantity());

        inventoryService.reserveInventory(event);

        System.out.println("========================");
    }
}
