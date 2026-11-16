package com.example.paymentservice.service;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.event.OrderCreatedEvent;
import com.example.paymentservice.event.PaymentCompletedEvent;
import com.example.paymentservice.outbox.PaymentOutbox;
import com.example.paymentservice.outbox.PaymentOutboxRepository;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.repository.ProcessedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentProcessingService {

    private final PaymentRepository paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final PaymentOutboxRepository paymentOutboxRepository;

    public PaymentProcessingService(
            PaymentRepository paymentRepository,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper,
            PaymentOutboxRepository paymentOutboxRepository) {

        this.paymentRepository = paymentRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper=objectMapper;
        this.paymentOutboxRepository=paymentOutboxRepository;
    }

    @Transactional
    public void process(OrderCreatedEvent event) throws JsonProcessingException {

        processedEventRepository.save(
                new ProcessedEvent(event.getEventId())
        );

        Payment payment = new Payment();

        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getPrice());
        payment.setPaymentMethod("UPI");
        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);
        System.out.println("Payment Created : " + payment.getId());

        // Create the event
        PaymentCompletedEvent completed = new PaymentCompletedEvent(
                event.getOrderId(),
                "SUCCESS"
        );

        PaymentOutbox outbox = new PaymentOutbox();

        outbox.setAggregateId(
                String.valueOf(event.getOrderId())
        );

        outbox.setEventType("PaymentCompleted");

        outbox.setPayload(
                objectMapper.writeValueAsString(completed)
        );

        outbox.setStatus("NEW");

        paymentOutboxRepository.save(outbox);
    }
}