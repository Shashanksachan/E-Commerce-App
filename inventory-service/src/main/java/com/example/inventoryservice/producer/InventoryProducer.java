package com.example.inventoryservice.producer;

import com.example.inventoryservice.event.InventoryReservedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {

    private final KafkaTemplate<String, InventoryReservedEvent> kafkaTemplate;

    public InventoryProducer(KafkaTemplate<String, InventoryReservedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(InventoryReservedEvent event) {
        kafkaTemplate.send(
                "inventory-reserved",
                String.valueOf(event.getOrderId()),
                event
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Inventory Event Published");
                System.out.println("Partition : "
                        + result.getRecordMetadata().partition());
                System.out.println("Offset : "
                        + result.getRecordMetadata().offset());
            } else {
                ex.printStackTrace();
            }

        });
    }
}