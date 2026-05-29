package com.tms.backend.dto;

import com.tms.backend.entity.TripStatus;

public class UpdateTripRequest {
    private Long vehicleId;
    private Long driverId;
    private TripStatus status;
    private Boolean generateInvoice;
    private java.time.LocalDateTime endTime;
    private java.math.BigDecimal fuelExpense;
    private java.math.BigDecimal tollExpense;
    private java.time.LocalDateTime departureTime;
    private java.time.LocalDateTime estimatedArrivalTime;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public Boolean getGenerateInvoice() {
        return generateInvoice;
    }

    public void setGenerateInvoice(Boolean generateInvoice) {
        this.generateInvoice = generateInvoice;
    }

    public java.time.LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(java.time.LocalDateTime endTime) { this.endTime = endTime; }
    
    public java.math.BigDecimal getFuelExpense() { return fuelExpense; }
    public void setFuelExpense(java.math.BigDecimal fuelExpense) { this.fuelExpense = fuelExpense; }
    
    public java.math.BigDecimal getTollExpense() { return tollExpense; }
    public void setTollExpense(java.math.BigDecimal tollExpense) { this.tollExpense = tollExpense; }

    public java.time.LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(java.time.LocalDateTime departureTime) { this.departureTime = departureTime; }

    public java.time.LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(java.time.LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }

    private java.util.Map<Long, CreateTripRequest.OrderUpdateDto> orderUpdates;
    public java.util.Map<Long, CreateTripRequest.OrderUpdateDto> getOrderUpdates() { return orderUpdates; }
    public void setOrderUpdates(java.util.Map<Long, CreateTripRequest.OrderUpdateDto> orderUpdates) { this.orderUpdates = orderUpdates; }

    private java.util.List<Long> removedOrderIds;
    public java.util.List<Long> getRemovedOrderIds() { return removedOrderIds; }
    public void setRemovedOrderIds(java.util.List<Long> removedOrderIds) { this.removedOrderIds = removedOrderIds; }

    private String note;
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
