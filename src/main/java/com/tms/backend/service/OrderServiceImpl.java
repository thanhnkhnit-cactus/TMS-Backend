package com.tms.backend.service;

import com.tms.backend.dto.CreateOrderRequest;
import com.tms.backend.entity.Customer;
import com.tms.backend.entity.Order;
import com.tms.backend.entity.OrderStatus;
import com.tms.backend.repository.CustomerRepository;
import com.tms.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Order createOrder(CreateOrderRequest request) {
        Customer customer;
        if (Boolean.TRUE.equals(request.getIsNewCustomer())) {
            customer = new Customer();
            customer.setCustomerCode("KH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            customer.setName(request.getNewCustomerName());
            customer.setPhone(request.getNewCustomerPhone());
            customer.setAddress(request.getNewCustomerAddress());
            customer.setCustomerType(com.tms.backend.entity.CustomerType.NEW);
            customer.setDebtLimit(java.math.BigDecimal.ZERO);
            customer = customerRepository.save(customer);
        } else {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        Order order = new Order();
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        order.setCustomerCode(customer.getCustomerCode());
        order.setCustomerName(customer.getName());
        order.setCustomerPhone(customer.getPhone());
        order.setCustomerAddress(customer.getAddress());
        
        order.setPickupAddress(request.getPickupAddress());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setGoodsName(request.getGoodsName());
        order.setWeightKg(request.getWeightKg());
        order.setCodAmount(request.getCodAmount());
        order.setDistanceKm(request.getDistanceKm());
        order.setSurcharge(request.getSurcharge());
        order.setDescription(request.getDescription());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Long id, CreateOrderRequest request) {
        Order existing = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (existing.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Không thể sửa đơn hàng đã hoàn thành");
        }
        
        Customer customer;
        if (Boolean.TRUE.equals(request.getIsNewCustomer())) {
            customer = new Customer();
            customer.setCustomerCode("KH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            customer.setName(request.getNewCustomerName());
            customer.setPhone(request.getNewCustomerPhone());
            customer.setAddress(request.getNewCustomerAddress());
            customer.setCustomerType(com.tms.backend.entity.CustomerType.NEW);
            customer.setDebtLimit(java.math.BigDecimal.ZERO);
            customer = customerRepository.save(customer);
        } else {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }
                
        existing.setCustomer(customer);
        existing.setCustomerCode(customer.getCustomerCode());
        existing.setCustomerName(customer.getName());
        existing.setCustomerPhone(customer.getPhone());
        existing.setCustomerAddress(customer.getAddress());
        
        existing.setPickupAddress(request.getPickupAddress());
        existing.setDeliveryAddress(request.getDeliveryAddress());
        existing.setGoodsName(request.getGoodsName());
        existing.setWeightKg(request.getWeightKg());
        existing.setCodAmount(request.getCodAmount());
        existing.setDistanceKm(request.getDistanceKm());
        existing.setSurcharge(request.getSurcharge());
        existing.setDescription(request.getDescription());
        
        return orderRepository.save(existing);
    }

    @Override
    public void deleteOrder(Long id) {
        Order existing = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (existing.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Không thể xóa đơn hàng đã hoàn thành");
        }
        existing.setIsDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        orderRepository.save(existing);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAllByStatus(status);
    }

    @Override
    @Transactional
    public void approveOrders(List<Long> orderIds) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.NEW);
            }
        }
        orderRepository.saveAll(orders);
    }

    @Override
    @Transactional
    public void rejectOrders(List<Long> orderIds, String reason) {
        List<Order> orders = orderRepository.findAllById(orderIds);
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.CANCELLED);
                order.setRejectReason(reason);
            }
        }
        orderRepository.saveAll(orders);
    }
}
