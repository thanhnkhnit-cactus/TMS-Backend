package com.tms.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_histories")
public class TripHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    private String fieldName; // "DRIVER", "VEHICLE", "STATUS"
    
    private Long oldValueId;
    private String oldValueName;
    
    private Long newValueId;
    private String newValueName;

    private LocalDateTime changedAt;

    public TripHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public Long getOldValueId() { return oldValueId; }
    public void setOldValueId(Long oldValueId) { this.oldValueId = oldValueId; }
    
    public String getOldValueName() { return oldValueName; }
    public void setOldValueName(String oldValueName) { this.oldValueName = oldValueName; }
    
    public Long getNewValueId() { return newValueId; }
    public void setNewValueId(Long newValueId) { this.newValueId = newValueId; }
    
    public String getNewValueName() { return newValueName; }
    public void setNewValueName(String newValueName) { this.newValueName = newValueName; }
    
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
