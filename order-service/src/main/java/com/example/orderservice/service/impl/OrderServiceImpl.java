package com.example.orderservice.service.impl;

import com.example.orderservice.client.PaymentFeignClient;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.PaymentResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.outbox.OutboxEvent;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.exception.ResourceNotFoundException;
import com.example.orderservice.producer.OrderProducer;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.outbox.OutboxRepository;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final PaymentFeignClient paymentFeignClient;

    private final OrderProducer orderProducer;

    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            PaymentFeignClient paymentFeignClient,
                            OrderProducer orderProducer,
                            OutboxRepository outboxRepository,
                            ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.paymentFeignClient = paymentFeignClient;
        this.orderProducer = orderProducer;
        this.outboxRepository=outboxRepository;
        this.objectMapper=objectMapper;
    }

    @Override
    @Retry(name = "paymentRetry")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public OrderResponse createOrder(OrderRequest request) {

        Order order = new Order();

        order.setCustomerName(request.getCustomerName());
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        order.setStatus("CREATED");

        Order savedOrder = orderRepository.save(order);

//        orderProducer.publish(
//                new OrderCreatedEvent(
//                        savedOrder.getId(),
//                        savedOrder.getProductName(),
//                        savedOrder.getQuantity(),
//                        savedOrder.getPrice()
//                )
//        );
//
        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        UUID.randomUUID().toString(),
                        savedOrder.getId(),
                        savedOrder.getProductName(),
                        savedOrder.getQuantity(),
                        savedOrder.getPrice()
                );

        OutboxEvent outbox = new OutboxEvent();

        outbox.setEventType("OrderCreated");
        outbox.setAggregateId(savedOrder.getId());
        outbox.setPayload(convertToJson(event));
        outbox.setStatus("NEW");
        outbox.setCreatedAt(LocalDateTime.now());

        outboxRepository.save(outbox);
//        PaymentRequest paymentRequest = new PaymentRequest();
//
//        paymentRequest.setOrderId(savedOrder.getId());
//        paymentRequest.setAmount(savedOrder.getPrice());
//        paymentRequest.setPaymentMethod("UPI");
//
//        PaymentResponse paymentResponse = paymentFeignClient.createPayment(paymentRequest);
        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id : " + id));

        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id : " + id));

        order.setCustomerName(request.getCustomerName());
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());

        Order updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id : " + id));

        orderRepository.delete(order);
    }

    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setProductName(order.getProductName());
        response.setQuantity(order.getQuantity());
        response.setPrice(order.getPrice());
        response.setStatus(order.getStatus());

        return response;
    }

    private OrderResponse mapToResponse(Order order, PaymentResponse paymentResponse) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setProductName(order.getProductName());
        response.setQuantity(order.getQuantity());
        response.setPrice(order.getPrice());
        response.setStatus(order.getStatus());
        response.setPayment(paymentResponse);

        return response;
    }

    public OrderResponse paymentFallback(OrderRequest request, Exception ex) {
        OrderResponse response = new OrderResponse();

        response.setCustomerName(request.getCustomerName());
        response.setProductName(request.getProductName());
        response.setQuantity(request.getQuantity());
        response.setPrice(request.getPrice());
        response.setStatus("PAYMENT_SERVICE_UNAVAILABLE");

        return response;
    }

    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON Serialization Failed", e);
        }
    }

    @Transactional
    public void updateInventoryReserved(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        if (!"CREATED".equals(order.getStatus())) {
            System.out.println("Ignoring InventoryReserved event. Current status = " + order.getStatus());
            return;
        }

        order.setStatus("INVENTORY_RESERVED");

        orderRepository.save(order);

        System.out.println("Order Updated Successfully");
    }

    @Transactional
    public void updatePaymentCompleted(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElse(null);

        if (order == null) {
            System.out.println("Order not found : " + orderId);
            return;
        }

        order.setStatus("PAID");

        orderRepository.save(order);

        System.out.println("Order updated to PAID : " + order.getId());
    }
}