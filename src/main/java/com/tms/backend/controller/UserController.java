package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.User;
import com.tms.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USER_READ')")
    public ApiResponse<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        if (search != null && !search.trim().isEmpty()) {
            return ApiResponse.success("Lấy danh sách thành công", 
                userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable));
        }
        return ApiResponse.success("Lấy danh sách thành công", userRepository.findAll(pageable));
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USER_CREATE')")
    public ApiResponse<User> createUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if(user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode("123456"));
        } else {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return ApiResponse.success("Tạo người dùng thành công", userRepository.save(user));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USER_UPDATE')")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User updated) {
        if (userRepository.existsByUsernameAndIdNot(updated.getUsername(), id)) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setRole(updated.getRole());
        existing.setStatus(updated.getStatus());
        if(updated.getPasswordHash() != null && !updated.getPasswordHash().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(updated.getPasswordHash()));
        }
        return ApiResponse.success("Cập nhật thành công", userRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('USER_DELETE')")
    public ApiResponse<?> deleteUser(@PathVariable Long id) {
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        userRepository.save(existing);
        return ApiResponse.success("Xóa người dùng thành công", null);
    }
}
