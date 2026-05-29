package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.User;
import com.tms.backend.repository.UserRepository;
import com.tms.backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        java.util.List<User> users = userRepository.findByUsername(request.username);
        if (users.isEmpty() || !users.get(0).getStatus()) {
            return ApiResponse.error("User not found or disabled");
        }
        User user = users.get(0);

        if (!passwordEncoder.matches(request.password, user.getPasswordHash())) {
            return ApiResponse.error("Invalid password");
        }

        String role = user.getRole() != null ? user.getRole().getRoleCode() : "";
        java.util.List<String> permissions = user.getRole() != null && user.getRole().getPermissions() != null 
                ? user.getRole().getPermissions() 
                : java.util.Collections.emptyList();
        
        String token = jwtUtil.generateToken(user.getUsername(), role, permissions);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", role);
        data.put("permissions", permissions);

        return ApiResponse.success("Login successful", data);
    }
}
