package com.tms.backend.service;

import com.tms.backend.dto.CreateOrderRequest;
import com.tms.backend.entity.Order;
import com.tms.backend.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderRequest request);
    Order updateOrder(Long id, CreateOrderRequest request);
    void deleteOrder(Long id);
    Order getOrder(Long id);
    Order updateOrderStatus(Long id, OrderStatus status);
    List<Order> getOrdersByStatus(OrderStatus status);
    void approveOrders(List<Long> orderIds);
    void rejectOrders(List<Long> orderIds, String reason);
}
