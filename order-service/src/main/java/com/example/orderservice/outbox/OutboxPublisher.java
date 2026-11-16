package com.example.orderservice.outbox;

import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.producer.OrderProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxPublisher {

    private final OutboxRepository repository;

    private final OrderProducer producer;

    private final ObjectMapper mapper;

    public OutboxPublisher(
            OutboxRepository repository,
            OrderProducer producer,
            ObjectMapper mapper) {

        this.repository = repository;
        this.producer = producer;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publish() throws Exception {

        List<OutboxEvent> events =
                repository.findByStatus("NEW");

        for (OutboxEvent event : events) {
            OrderCreatedEvent orderEvent =
                    mapper.readValue(
                            event.getPayload(),
                            OrderCreatedEvent.class);

            producer.publish(orderEvent);

            event.setStatus("SENT");

            repository.save(event);
        }
    }
}