package com.example.orderservice.consumer;

import com.example.orderservice.event.InventoryReservedEvent;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class InventoryReservedConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public InventoryReservedConsumer(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper=objectMapper;
    }

    @KafkaListener(
            topics = "inventory-reserved",
            groupId = "order-group"
    )
    public void consume(String message, Acknowledgment ack) throws JsonProcessingException {
        InventoryReservedEvent event =
                objectMapper.readValue(message, InventoryReservedEvent.class);

        System.out.println("======================");
        System.out.println("Inventory Reserved");
        System.out.println("Order Id : " + event.getOrderId());

        orderService.updateInventoryReserved(event.getOrderId());
        ack.acknowledge();
        System.out.println("======================");
    }
}