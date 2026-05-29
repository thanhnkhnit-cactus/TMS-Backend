package com.tms.backend.repository;

import com.tms.backend.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByRoleCode(String roleCode);
    boolean existsByRoleCode(String roleCode);
    boolean existsByRoleCodeAndIdNot(String roleCode, Long id);
    Page<Role> findByRoleNameContainingIgnoreCaseOrRoleCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}
