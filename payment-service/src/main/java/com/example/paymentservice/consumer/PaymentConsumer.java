package com.example.paymentservice.consumer;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.ProcessedEvent;
import com.example.paymentservice.event.OrderCreatedEvent;
import com.example.paymentservice.event.PaymentCompletedEvent;
import com.example.paymentservice.producer.PaymentProducer;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.repository.ProcessedEventRepository;
import com.example.paymentservice.service.PaymentProcessingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final PaymentProcessingService paymentProcessingService;

    private final ProcessedEventRepository processedEventRepository;

    public PaymentConsumer(
            PaymentRepository paymentRepository,
            PaymentProducer paymentProducer,
            ProcessedEventRepository processedEventRepository,
            PaymentProcessingService paymentProcessingService) {

        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
        this.processedEventRepository = processedEventRepository;
        this.paymentProcessingService=paymentProcessingService;
    }


    @Transactional
    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void consume(OrderCreatedEvent event,
                        ConsumerRecord<String, OrderCreatedEvent> record,
                        Acknowledgment ack) {
        System.out.println("======================");
        System.out.println("Partition : " + record.partition());
        System.out.println("Offset    : " + record.offset());
        System.out.println("Order Id  : " + event.getOrderId());

        try {
            paymentProcessingService.process(event);
        } catch (Exception ex) {
            System.out.println("Duplicate Event");
            ack.acknowledge();
        }
    }

//    @KafkaListener(topics = "order-created", groupId = "payment-group")
//    public void consume(OrderCreatedEvent event, Acknowledgment ack) {
//
//        System.out.println("Message Received");
//        try {
//            throw new RuntimeException("Testing DLQ");
//
//        } finally {
//            ack.acknowledge();
//        }
//    }
}