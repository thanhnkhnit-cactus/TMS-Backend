package com.tms.backend.repository;

import com.tms.backend.entity.TripHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripHistoryRepository extends JpaRepository<TripHistory, Long> {
    List<TripHistory> findByTripIdOrderByChangedAtDesc(Long tripId);
}
