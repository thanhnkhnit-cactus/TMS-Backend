package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.Customer;
import com.tms.backend.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('CUSTOMER_READ', 'ORDER_CREATE', 'ORDER_UPDATE', 'ORDER_READ', 'INVOICE_CREATE', 'INVOICE_UPDATE', 'INVOICE_READ')")
    public ApiResponse<org.springframework.data.domain.Page<Customer>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        return ApiResponse.success(customerService.getCustomers(search, pageable));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('CUSTOMER_CREATE')")
    public ApiResponse<Customer> createCustomer(@RequestBody Customer customer) {
        return ApiResponse.success("Customer created", customerService.createCustomer(customer));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    public ApiResponse<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return ApiResponse.success("Customer updated", customerService.updateCustomer(id, customer));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
    public ApiResponse<?> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.success("Customer deleted", null);
    }
}
