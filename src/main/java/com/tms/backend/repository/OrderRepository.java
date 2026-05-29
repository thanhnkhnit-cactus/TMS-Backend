package com.tms.backend.repository;

import com.tms.backend.entity.Order;
import com.tms.backend.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);
    boolean existsByOrderCode(String orderCode);
    boolean existsByOrderCodeAndIdNot(String orderCode, Long id);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    java.util.List<Order> findAllByStatus(OrderStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o WHERE " +
        "(:status IS NULL OR o.status = :status) AND " +
        "(:search IS NULL OR :search = '' OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(o.customer.phone) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(o.pickupAddress) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(o.deliveryAddress) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "OR LOWER(o.goodsName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
        "ORDER BY o.createdAt DESC")
    Page<Order> findBySearchAndStatus(@org.springframework.data.repository.query.Param("search") String search, 
                                      @org.springframework.data.repository.query.Param("status") OrderStatus status, 
                                      Pageable pageable);
}
