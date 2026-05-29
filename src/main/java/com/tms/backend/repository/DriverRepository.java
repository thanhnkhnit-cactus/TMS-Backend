package com.tms.backend.repository;

import com.tms.backend.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByCccd(String cccd);
    boolean existsByCccd(String cccd);
    boolean existsByCccdAndIdNot(String cccd, Long id);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM Driver d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:search = '' OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.licenseNo) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY d.createdAt DESC")
    org.springframework.data.domain.Page<Driver> findBySearch(@org.springframework.data.repository.query.Param("search") String search, @org.springframework.data.repository.query.Param("status") com.tms.backend.entity.DriverStatus status, org.springframework.data.domain.Pageable pageable);
}
