package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.Invoice;
import com.tms.backend.repository.InvoiceRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('INVOICE_READ')")
    public ApiResponse<?> getAllInvoices(
            @RequestParam(required = false, defaultValue = "false") boolean paginate,
            @RequestParam(required = false, defaultValue = "") String search,
            @org.springframework.data.web.PageableDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        if (paginate) {
            return ApiResponse.success(invoiceRepository.findBySearch(search, pageable));
        }
        return ApiResponse.success(invoiceRepository.findAll());
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('INVOICE_CREATE')")
    public ApiResponse<Invoice> createInvoice(@RequestBody Invoice invoice) {
        if (invoice.getInvoiceCode() != null && invoiceRepository.existsByInvoiceCode(invoice.getInvoiceCode())) {
            throw new RuntimeException("Mã hóa đơn đã tồn tại");
        }
        invoice.setCreatedAt(LocalDateTime.now());
        if(invoice.getStatus() == null) invoice.setStatus("UNPAID");
        return ApiResponse.success("Invoice created", invoiceRepository.save(invoice));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('INVOICE_UPDATE')")
    public ApiResponse<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice updated) {
        if (updated.getInvoiceCode() != null && invoiceRepository.existsByInvoiceCodeAndIdNot(updated.getInvoiceCode(), id)) {
            throw new RuntimeException("Mã hóa đơn đã tồn tại");
        }
        Invoice existing = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        if(updated.getOrder() != null && updated.getOrder().getId() != null) {
            existing.setOrder(updated.getOrder());
        }
        existing.setInvoiceCode(updated.getInvoiceCode());
        existing.setTotalAmount(updated.getTotalAmount());
        existing.setPaidAmount(updated.getPaidAmount());
        existing.setStatus(updated.getStatus());
        existing.setDueDate(updated.getDueDate());
        return ApiResponse.success("Invoice updated", invoiceRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('INVOICE_DELETE')")
    public ApiResponse<?> deleteInvoice(@PathVariable Long id) {
        Invoice existing = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        invoiceRepository.save(existing);
        return ApiResponse.success("Invoice deleted", null);
    }
}
