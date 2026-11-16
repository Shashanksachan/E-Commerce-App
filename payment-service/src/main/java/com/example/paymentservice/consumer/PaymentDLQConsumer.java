package com.example.paymentservice.consumer;

import com.example.paymentservice.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentDLQConsumer {

    @KafkaListener(topics = "order-created-dlt", groupId = "payment-dlt-group")
    public void consume(OrderCreatedEvent event) {

        System.out.println("======================================");
        System.out.println("MESSAGE RECEIVED FROM DLT");
        System.out.println("Order Id : " + event.getOrderId());
        System.out.println("Product  : " + event.getProductName());
        System.out.println("Quantity : " + event.getQuantity());
        System.out.println("Price    : " + event.getPrice());
        System.out.println("======================================");
    }
}