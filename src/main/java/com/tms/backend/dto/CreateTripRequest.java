package com.tms.backend.dto;

import java.util.List;

public class CreateTripRequest {
    private Long vehicleId;
    private Long driverId;
    private List<Long> orderIds;
    private java.time.LocalDateTime departureTime;
    private java.time.LocalDateTime estimatedArrivalTime;
    public static class OrderUpdateDto {
        private java.math.BigDecimal distanceKm;
        private java.math.BigDecimal codAmount;
        private java.math.BigDecimal surcharge;

        public java.math.BigDecimal getDistanceKm() { return distanceKm; }
        public void setDistanceKm(java.math.BigDecimal distanceKm) { this.distanceKm = distanceKm; }
        public java.math.BigDecimal getCodAmount() { return codAmount; }
        public void setCodAmount(java.math.BigDecimal codAmount) { this.codAmount = codAmount; }
        public java.math.BigDecimal getSurcharge() { return surcharge; }
        public void setSurcharge(java.math.BigDecimal surcharge) { this.surcharge = surcharge; }
    }

    private java.util.Map<Long, OrderUpdateDto> orderUpdates;
    
    public CreateTripRequest() {}

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public List<Long> getOrderIds() { return orderIds; }
    public void setOrderIds(List<Long> orderIds) { this.orderIds = orderIds; }
    public java.time.LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(java.time.LocalDateTime departureTime) { this.departureTime = departureTime; }
    public java.time.LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public void setEstimatedArrivalTime(java.time.LocalDateTime estimatedArrivalTime) { this.estimatedArrivalTime = estimatedArrivalTime; }
    public java.util.Map<Long, OrderUpdateDto> getOrderUpdates() { return orderUpdates; }
    public void setOrderUpdates(java.util.Map<Long, OrderUpdateDto> orderUpdates) { this.orderUpdates = orderUpdates; }
}
