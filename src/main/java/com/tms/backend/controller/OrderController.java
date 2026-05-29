package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.dto.CreateOrderRequest;
import com.tms.backend.dto.RejectOrdersRequest;
import com.tms.backend.entity.Order;
import com.tms.backend.entity.OrderStatus;
import com.tms.backend.repository.OrderRepository;
import com.tms.backend.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ORDER_READ', 'TRIP_CREATE', 'TRIP_UPDATE', 'TRIP_READ')")
    public ApiResponse<Page<Order>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false, defaultValue = "") String search,
            @org.springframework.data.web.PageableDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        if (search == null) search = "";
        return ApiResponse.success(orderRepository.findBySearchAndStatus(search, status, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrder(id));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ORDER_CREATE')")
    public ApiResponse<Order> createOrder(@RequestBody CreateOrderRequest request) {
        return ApiResponse.success("Order created successfully", orderService.createOrder(request));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ORDER_UPDATE')")
    public ApiResponse<Order> updateOrder(@PathVariable Long id, @RequestBody CreateOrderRequest request) {
        return ApiResponse.success("Order updated successfully", orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ORDER_DELETE')")
    public ApiResponse<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success("Order deleted successfully", null);
    }

    @PatchMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ORDER_UPDATE')")
    public ApiResponse<Order> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ApiResponse.success("Status updated", orderService.updateOrderStatus(id, status));
    }

    @PutMapping("/approve")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ORDER_APPROVE')")
    public ApiResponse<?> approveOrders(@RequestBody java.util.List<Long> orderIds) {
        orderService.approveOrders(orderIds);
        return ApiResponse.success("Duyệt đơn hàng thành công", null);
    }

    @PutMapping("/reject")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ORDER_APPROVE')")
    public ApiResponse<?> rejectOrders(@RequestBody RejectOrdersRequest request) {
        orderService.rejectOrders(request.getOrderIds(), request.getReason());
        return ApiResponse.success("Từ chối đơn hàng thành công", null);
    }
}
