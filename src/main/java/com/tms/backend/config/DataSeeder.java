package com.tms.backend.config;

import com.tms.backend.entity.Role;
import com.tms.backend.entity.User;
import com.tms.backend.repository.RoleRepository;
import com.tms.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Khởi tạo Role ADMIN
            java.util.List<Role> adminRoles = roleRepository.findByRoleCode("ADMIN");
            Role adminRole;
            if (adminRoles.isEmpty()) {
                adminRole = new Role();
                adminRole.setRoleCode("ADMIN");
                adminRole.setRoleName("Quản trị viên hệ thống");
                System.out.println("Đã khởi tạo Role ADMIN mặc định!");
            } else {
                adminRole = adminRoles.get(0);
                // Soft delete các Role bị trùng lặp để tránh lỗi Foreign Key
                for (int i = 1; i < adminRoles.size(); i++) {
                    Role duplicate = adminRoles.get(i);
                    duplicate.setIsDeleted(true);
                    roleRepository.save(duplicate);
                }
            }
            
            // Luôn cập nhật full quyền cho ADMIN để tránh lỗi thiếu quyền khi chạy lại
            adminRole.setPermissions(Arrays.asList(
                "USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE",
                "ROLE_READ", "ROLE_CREATE", "ROLE_UPDATE", "ROLE_DELETE",
                "ORDER_READ", "ORDER_CREATE", "ORDER_UPDATE", "ORDER_DELETE", "ORDER_APPROVE",
                "CUSTOMER_READ", "CUSTOMER_CREATE", "CUSTOMER_UPDATE", "CUSTOMER_DELETE",
                "TRIP_READ", "TRIP_CREATE", "TRIP_UPDATE", "TRIP_DELETE", "TRIP_APPROVE",
                "VEHICLE_READ", "VEHICLE_CREATE", "VEHICLE_UPDATE", "VEHICLE_DELETE",
                "DRIVER_READ", "DRIVER_CREATE", "DRIVER_UPDATE", "DRIVER_DELETE",
                "EXPENSE_READ", "EXPENSE_CREATE", "EXPENSE_UPDATE", "EXPENSE_DELETE", "EXPENSE_APPROVE",
                "INVOICE_READ", "INVOICE_CREATE", "INVOICE_UPDATE", "INVOICE_DELETE", "INVOICE_APPROVE"
            ));
            roleRepository.save(adminRole);

            // Khởi tạo User admin
            java.util.List<User> admins = userRepository.findByUsername("admin");
            User admin;
            if (admins.isEmpty()) {
                admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@tms.com");
                admin.setPhone("0988888888");
                admin.setStatus(true);
            } else {
                admin = admins.get(0);
                for (int i = 1; i < admins.size(); i++) {
                    User duplicate = admins.get(i);
                    duplicate.setIsDeleted(true);
                    userRepository.save(duplicate);
                }
            }
            // Force reset mật khẩu thành "admin" để fix lỗi đăng nhập
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            
            admin.setRole(adminRole);
            userRepository.save(admin);
            System.out.println("Đã cấu hình tài khoản admin/admin với Role ADMIN!");
        };
    }
}
