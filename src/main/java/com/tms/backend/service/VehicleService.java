package com.tms.backend.service;

import com.tms.backend.entity.Vehicle;
import com.tms.backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    public org.springframework.data.domain.Page<Vehicle> getVehiclesPage(String search, com.tms.backend.entity.VehicleStatus status, org.springframework.data.domain.Pageable pageable) {
        if (search == null) search = "";
        return vehicleRepository.findBySearch(search, status, pageable);
    }
    
    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicleRepository.existsByPlateNumber(vehicle.getPlateNumber())) {
            throw new RuntimeException("Biển số xe đã tồn tại");
        }
        vehicle.setCreatedAt(LocalDateTime.now());
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle updated) {
        if (vehicleRepository.existsByPlateNumberAndIdNot(updated.getPlateNumber(), id)) {
            throw new RuntimeException("Biển số xe đã tồn tại");
        }
        Vehicle existing = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        existing.setPlateNumber(updated.getPlateNumber());
        existing.setVehicleType(updated.getVehicleType());
        existing.setCapacityKg(updated.getCapacityKg());
        existing.setCodFeePerKm(updated.getCodFeePerKm());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());
        existing.setInspectionExpiry(updated.getInspectionExpiry());
        return vehicleRepository.save(existing);
    }
    
    public void deleteVehicle(Long id) {
        Vehicle existing = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        vehicleRepository.save(existing);
    }
}
