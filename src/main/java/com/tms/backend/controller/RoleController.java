package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.Role;
import com.tms.backend.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_READ')")
    public ApiResponse<Page<Role>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        if (search != null && !search.trim().isEmpty()) {
            return ApiResponse.success("Lấy danh sách vai trò thành công", 
                    roleRepository.findByRoleNameContainingIgnoreCaseOrRoleCodeContainingIgnoreCase(search, search, pageable));
        }
        return ApiResponse.success("Lấy danh sách vai trò thành công", roleRepository.findAll(pageable));
    }

    @GetMapping("/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('ROLE_READ', 'USER_CREATE', 'USER_UPDATE', 'USER_READ')")
    public ApiResponse<java.util.List<Role>> getAllRoles() {
        return ApiResponse.success("Lấy danh sách tất cả vai trò", roleRepository.findAll());
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        if (roleRepository.existsByRoleCode(role.getRoleCode())) {
            throw new RuntimeException("Mã vai trò đã tồn tại");
        }
        return ApiResponse.success("Tạo vai trò thành công", roleRepository.save(role));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role roleDetails) {
        if (roleRepository.existsByRoleCodeAndIdNot(roleDetails.getRoleCode(), id)) {
            throw new RuntimeException("Mã vai trò đã tồn tại");
        }
        Role role = roleRepository.findById(id).orElseThrow();
        role.setRoleCode(roleDetails.getRoleCode());
        role.setRoleName(roleDetails.getRoleName());
        role.setPermissions(roleDetails.getPermissions());
        return ApiResponse.success("Cập nhật vai trò thành công", roleRepository.save(role));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ApiResponse<?> deleteRole(@PathVariable Long id) {
        Role role = roleRepository.findById(id).orElseThrow();
        role.setIsDeleted(true);
        role.setDeletedAt(java.time.LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        role.setDeletedBy(currentUser);
        roleRepository.save(role);
        return ApiResponse.success("Xóa vai trò thành công", null);
    }
}
