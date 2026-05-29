package com.tms.backend.service;

import com.tms.backend.entity.Driver;
import com.tms.backend.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverService {
    
    private final DriverRepository driverRepository;
    
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }
    
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    public org.springframework.data.domain.Page<Driver> getDriversPage(String search, com.tms.backend.entity.DriverStatus status, org.springframework.data.domain.Pageable pageable) {
        if (search == null) search = "";
        return driverRepository.findBySearch(search, status, pageable);
    }
    
    public Driver createDriver(Driver driver) {
        if (driverRepository.existsByCccd(driver.getCccd())) {
            throw new RuntimeException("Số CCCD đã tồn tại");
        }
        driver.setCreatedAt(LocalDateTime.now());
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, Driver updated) {
        if (driverRepository.existsByCccdAndIdNot(updated.getCccd(), id)) {
            throw new RuntimeException("Số CCCD đã tồn tại");
        }
        Driver existing = driverRepository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
        existing.setFullName(updated.getFullName());
        existing.setCccd(updated.getCccd());
        existing.setLicenseNo(updated.getLicenseNo());
        existing.setLicenseExpiry(updated.getLicenseExpiry());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setStatus(updated.getStatus());
        return driverRepository.save(existing);
    }
    
    public void deleteDriver(Long id) {
        Driver existing = driverRepository.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        driverRepository.save(existing);
    }
}
