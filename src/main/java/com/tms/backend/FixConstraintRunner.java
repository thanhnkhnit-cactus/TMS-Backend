package com.tms.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// @Component
public class FixConstraintRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public FixConstraintRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE invoices CASCADE;");
            jdbcTemplate.execute("ALTER TABLE invoices DROP COLUMN IF EXISTS customer_id;");
            System.out.println("==================================================");
            System.out.println("✅ CLEANED INVOICES AND DROPPED customer_id SUCCESSFULLY");
            System.out.println("==================================================");
        } catch (Exception e) {
            System.out.println("⚠️ Error cleaning invoices: " + e.getMessage());
        }
    }
}
