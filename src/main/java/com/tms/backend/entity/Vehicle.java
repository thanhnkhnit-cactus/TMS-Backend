package com.tms.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@org.hibernate.annotations.SQLRestriction("is_deleted = false")
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_vehicle_plate", columnList = "plateNumber"),
    @Index(name = "idx_vehicle_status", columnList = "status"),
    @Index(name = "idx_vehicle_created_at", columnList = "createdAt")
})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plateNumber;

    private String vehicleType;

    @Column(precision = 10, scale = 2)
    private BigDecimal capacityKg;

    @Column(precision = 15, scale = 2)
    private BigDecimal codFeePerKm;

    private String description;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private LocalDate inspectionExpiry;
    private LocalDateTime createdAt;
    
    private Boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public Vehicle() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public BigDecimal getCapacityKg() { return capacityKg; }
    public void setCapacityKg(BigDecimal capacityKg) { this.capacityKg = capacityKg; }
    
    public BigDecimal getCodFeePerKm() { return codFeePerKm; }
    public void setCodFeePerKm(BigDecimal codFeePerKm) { this.codFeePerKm = codFeePerKm; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    public LocalDate getInspectionExpiry() { return inspectionExpiry; }
    public void setInspectionExpiry(LocalDate inspectionExpiry) { this.inspectionExpiry = inspectionExpiry; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
}
