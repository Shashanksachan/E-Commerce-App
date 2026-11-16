package com.example.orderservice.producer;

import com.example.orderservice.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderCreatedEvent event) throws Exception {

        var result = kafkaTemplate.send(
                "order-created",
                String.valueOf(event.getOrderId()),
                event
        ).get();          // <-- Wait for Kafka ACK

        System.out.println("Published to partition : "
                + result.getRecordMetadata().partition());

        System.out.println("Offset : "
                + result.getRecordMetadata().offset());

        System.out.println("Published Event ID: " + event);
        System.out.println("Published Event for Order ID: " + event.getOrderId());
    }

    @Override
    public String toString() {
        return "OrderProducer{" +
                "kafkaTemplate=" + kafkaTemplate +
                '}';
    }
}