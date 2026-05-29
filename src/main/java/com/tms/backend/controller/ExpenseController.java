package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.Expense;
import com.tms.backend.repository.ExpenseRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final JdbcTemplate jdbcTemplate;

    public ExpenseController(ExpenseRepository expenseRepository, JdbcTemplate jdbcTemplate) {
        this.expenseRepository = expenseRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void fixSchema() {
        try {
            jdbcTemplate.execute("ALTER TABLE expenses ALTER COLUMN trip_id DROP NOT NULL");
        } catch (Exception e) {
            System.out.println("Could not alter table expenses: " + e.getMessage());
        }
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('EXPENSE_READ')")
    public ApiResponse<?> getAllExpenses(
            @RequestParam(required = false, defaultValue = "false") boolean paginate,
            @RequestParam(required = false, defaultValue = "") String search,
            @org.springframework.data.web.PageableDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        if (paginate) {
            return ApiResponse.success(expenseRepository.findBySearch(search, pageable));
        }
        return ApiResponse.success(expenseRepository.findAll());
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('EXPENSE_CREATE')")
    public ApiResponse<Expense> createExpense(@RequestBody Expense expense) {
        validateExpense(expense);
        expense.setCreatedAt(LocalDateTime.now());
        return ApiResponse.success("Expense recorded", expenseRepository.save(expense));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('EXPENSE_UPDATE')")
    public ApiResponse<Expense> updateExpense(@PathVariable Long id, @RequestBody Expense updated) {
        Expense existing = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        
        existing.setTrip(updated.getTrip());
        existing.setVehicle(updated.getVehicle());
        existing.setExpenseType(updated.getExpenseType());
        existing.setAmount(updated.getAmount());
        existing.setNotes(updated.getNotes());
        existing.setExpenseDate(updated.getExpenseDate());
        existing.setAttachmentUrl(updated.getAttachmentUrl());
        
        validateExpense(existing);
        
        return ApiResponse.success("Expense updated", expenseRepository.save(existing));
    }

    private void validateExpense(Expense expense) {
        if (expense.getVehicle() == null || expense.getVehicle().getId() == null) {
            throw new RuntimeException("Phải chọn Xe cho chi phí này.");
        }
        // Bỏ validate bắt buộc chọn Chuyến xe theo yêu cầu
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('EXPENSE_DELETE')")
    public ApiResponse<?> deleteExpense(@PathVariable Long id) {
        Expense existing = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        expenseRepository.save(existing);
        return ApiResponse.success("Expense deleted", null);
    }
}
