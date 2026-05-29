package com.tms.backend.repository;

import com.tms.backend.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTripId(Long tripId);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM Expense e LEFT JOIN e.vehicle v LEFT JOIN e.trip t WHERE " +
           ":search = '' OR LOWER(e.notes) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.plateNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.tripCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY e.createdAt DESC")
    org.springframework.data.domain.Page<Expense> findBySearch(@org.springframework.data.repository.query.Param("search") String search, org.springframework.data.domain.Pageable pageable);
}
