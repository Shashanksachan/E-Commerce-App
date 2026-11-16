package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrder(Long id);

    List<OrderResponse> getAllOrders();

    OrderResponse updateOrder(Long id, OrderRequest request);

    void deleteOrder(Long id);

    void updateInventoryReserved(Long orderId);

    public void updatePaymentCompleted(Long orderId);
}