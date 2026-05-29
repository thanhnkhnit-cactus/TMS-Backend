package com.tms.backend.repository;

import com.tms.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByOrderId(Long orderId);
    boolean existsByInvoiceCode(String invoiceCode);
    boolean existsByInvoiceCodeAndIdNot(String invoiceCode, Long id);
    List<Invoice> findByOrderIdIn(java.util.List<Long> orderIds);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM Invoice i LEFT JOIN i.order o WHERE " +
           ":search = '' OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY i.createdAt DESC")
    org.springframework.data.domain.Page<Invoice> findBySearch(@org.springframework.data.repository.query.Param("search") String search, org.springframework.data.domain.Pageable pageable);
}
