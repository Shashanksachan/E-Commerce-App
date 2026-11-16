package com.example.inventoryservice.scheduler;

import com.example.inventoryservice.entity.InventoryOutbox;
import com.example.inventoryservice.event.InventoryReservedEvent;
import com.example.inventoryservice.producer.InventoryProducer;
import com.example.inventoryservice.repository.InventoryOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryOutboxScheduler {

    private final InventoryOutboxRepository outboxRepository;
    private final InventoryProducer inventoryProducer;
    private final ObjectMapper objectMapper;

    public InventoryOutboxScheduler(InventoryOutboxRepository outboxRepository,
                                    InventoryProducer inventoryProducer,
                                    ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.inventoryProducer = inventoryProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<InventoryOutbox> events =
                outboxRepository.findByStatus("NEW");

        for (InventoryOutbox outbox : events) {
            try {
                InventoryReservedEvent event =
                        objectMapper.readValue(
                                outbox.getPayload(),
                                InventoryReservedEvent.class);

                inventoryProducer.publish(event);

                outbox.setStatus("SENT");

                outboxRepository.save(outbox);

                System.out.println("Inventory Outbox Updated to SENT");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}