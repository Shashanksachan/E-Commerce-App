package com.example.paymentservice.outbox;

import com.example.paymentservice.event.PaymentCompletedEvent;
import com.example.paymentservice.producer.PaymentProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentOutboxScheduler {

    private final PaymentOutboxRepository paymentOutboxRepository;

    private final PaymentProducer paymentProducer;

    private final ObjectMapper objectMapper;

    public PaymentOutboxScheduler(
            PaymentOutboxRepository paymentOutboxRepository,
            PaymentProducer paymentProducer,
            ObjectMapper objectMapper) {

        this.paymentOutboxRepository = paymentOutboxRepository;
        this.paymentProducer = paymentProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() throws Exception {

        List<PaymentOutbox> events =
                paymentOutboxRepository.findByStatus("NEW");

        for (PaymentOutbox outbox : events) {

            PaymentCompletedEvent event =
                    objectMapper.readValue(
                            outbox.getPayload(),
                            PaymentCompletedEvent.class);

            paymentProducer.publish(event);

            outbox.setStatus("SENT");

            paymentOutboxRepository.save(outbox);
        }
    }
}
