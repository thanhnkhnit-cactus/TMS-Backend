package com.tms.backend.repository;

import com.tms.backend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerCode(String customerCode);
    boolean existsByCustomerCode(String customerCode);
    boolean existsByCustomerCodeAndIdNot(String customerCode, Long id);
    org.springframework.data.domain.Page<Customer> findByNameContainingIgnoreCaseOrCustomerCodeContainingIgnoreCaseOrPhoneContainingIgnoreCase(String name, String code, String phone, org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Customer c WHERE " +
           ":search = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.createdAt DESC")
    org.springframework.data.domain.Page<Customer> findBySearch(@org.springframework.data.repository.query.Param("search") String search, org.springframework.data.domain.Pageable pageable);
}
