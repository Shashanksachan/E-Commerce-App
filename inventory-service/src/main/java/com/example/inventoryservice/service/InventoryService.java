package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.entity.InventoryOutbox;
import com.example.inventoryservice.event.InventoryReservedEvent;
import com.example.inventoryservice.event.OrderCreatedEvent;
import com.example.inventoryservice.repository.InventoryOutboxRepository;
import com.example.inventoryservice.repository.InventoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryOutboxRepository outboxRepository,
                            ObjectMapper objectMapper) {

        this.inventoryRepository = inventoryRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public void reserveInventory(OrderCreatedEvent  event) {

        Inventory inventory = inventoryRepository.findByProductName(event.getProductName())
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        if (inventory.getAvailableQuantity() < event.getQuantity()) {
            throw new RuntimeException("Insufficient Stock");
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - event.getQuantity());

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + event.getQuantity());

        inventoryRepository.save(inventory);

        InventoryReservedEvent reservedEvent =
                new InventoryReservedEvent(
                        event.getOrderId(),
                        event.getProductName(),
                        event.getQuantity()
                );

        InventoryOutbox outbox = new InventoryOutbox();

        outbox.setAggregateId(event.getOrderId());

        outbox.setEventType("InventoryReserved");

        try {
            outbox.setPayload(objectMapper.writeValueAsString(reservedEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        outbox.setStatus("NEW");

        outbox.setCreatedAt(LocalDateTime.now());

        outboxRepository.save(outbox);

        System.out.println("Inventory Reserved Successfully");
        System.out.println("Outbox Event Created");

        System.out.println("Inventory Reserved Successfully");
    }
}