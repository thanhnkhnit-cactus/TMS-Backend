package com.tms.backend.repository;

import com.tms.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tms.backend.entity.TripStatus;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByTripCode(String tripCode);
    boolean existsByTripCode(String tripCode);
    boolean existsByTripCodeAndIdNot(String tripCode, Long id);

    @Query("SELECT t FROM Trip t WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(t.tripCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.vehicle.plateNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.driver.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.driver.phone) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) " +
           "ORDER BY t.createdAt DESC")
    org.springframework.data.domain.Page<Trip> searchTrips(@Param("search") String search, @Param("status") TripStatus status, org.springframework.data.domain.Pageable pageable);
}
