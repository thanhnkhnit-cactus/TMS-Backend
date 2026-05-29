package com.tms.backend.repository;

import com.tms.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByPlateNumber(String plateNumber);
    boolean existsByPlateNumber(String plateNumber);
    boolean existsByPlateNumberAndIdNot(String plateNumber, Long id);

    @org.springframework.data.jpa.repository.Query("SELECT v FROM Vehicle v WHERE " +
           "(:status IS NULL OR v.status = :status) AND " +
           "(:search = '' OR LOWER(v.plateNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(v.vehicleType) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY v.createdAt DESC")
    org.springframework.data.domain.Page<Vehicle> findBySearch(@org.springframework.data.repository.query.Param("search") String search, @org.springframework.data.repository.query.Param("status") com.tms.backend.entity.VehicleStatus status, org.springframework.data.domain.Pageable pageable);
}
