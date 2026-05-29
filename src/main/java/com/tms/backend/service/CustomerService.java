package com.tms.backend.service;

import com.tms.backend.entity.Customer;
import com.tms.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public org.springframework.data.domain.Page<Customer> getCustomers(String search, org.springframework.data.domain.Pageable pageable) {
        if (search == null) search = "";
        return customerRepository.findBySearch(search, pageable);
    }
    
    public Customer createCustomer(Customer customer) {
        if (customer.getCustomerCode() != null && customerRepository.existsByCustomerCode(customer.getCustomerCode())) {
            throw new RuntimeException("Mã khách hàng đã tồn tại");
        }
        if (customer.getCustomerCode() == null || customer.getCustomerCode().isEmpty()) {
            customer.setCustomerCode("KH-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        customer.setCreatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updated) {
        if (updated.getCustomerCode() != null && customerRepository.existsByCustomerCodeAndIdNot(updated.getCustomerCode(), id)) {
            throw new RuntimeException("Mã khách hàng đã tồn tại");
        }
        Customer existing = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        if (updated.getCustomerCode() != null && !updated.getCustomerCode().isEmpty()) {
            existing.setCustomerCode(updated.getCustomerCode());
        }
        existing.setDebtLimit(updated.getDebtLimit());
        existing.setCustomerType(updated.getCustomerType());
        return customerRepository.save(existing);
    }
    
    public void deleteCustomer(Long id) {
        Customer existing = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        customerRepository.save(existing);
    }
}
