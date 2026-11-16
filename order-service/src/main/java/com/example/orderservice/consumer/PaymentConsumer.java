package com.example.orderservice.consumer;

import com.example.orderservice.entity.Order;
import com.example.orderservice.event.PaymentCompletedEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    public PaymentConsumer(OrderRepository orderRepository,
                           ObjectMapper objectMapper,
                           OrderService orderService) {
        this.orderRepository = orderRepository;
        this.objectMapper=objectMapper;
        this.orderService=orderService;
    }

//    @KafkaListener(topics = "payment-completed", groupId = "order-group")
//    public void consume(String message) throws JsonProcessingException {
//        PaymentCompletedEvent event =
//                objectMapper.readValue(message, PaymentCompletedEvent.class);
//
//        Order order = orderRepository.findById(event.getOrderId())
//                .orElseThrow();
//
//        order.setStatus("PAID");
//
//        orderRepository.save(order);
//
//        System.out.println("Order updated to PAID : " + order.getId());
//    }

    @KafkaListener(topics = "payment-completed", groupId = "order-group")
    public void consume(String message,
                        Acknowledgment ack) throws JsonProcessingException {

        System.out.println("========== PAYMENT EVENT RECEIVED ==========");
        System.out.println(message);

        PaymentCompletedEvent event =
                objectMapper.readValue(message, PaymentCompletedEvent.class);

        orderService.updatePaymentCompleted(event.getOrderId());

        ack.acknowledge();

        System.out.println("Offset Committed");
    }
}