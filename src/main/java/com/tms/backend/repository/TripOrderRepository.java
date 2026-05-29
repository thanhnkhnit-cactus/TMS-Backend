package com.tms.backend.repository;

import com.tms.backend.entity.TripOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface TripOrderRepository extends JpaRepository<TripOrder, Long> {
    List<TripOrder> findByTripId(Long tripId);
    Optional<TripOrder> findByTripIdAndOrderId(Long tripId, Long orderId);
}
