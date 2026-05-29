package com.tms.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trip_orders")
public class TripOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Integer pickupSequence;
    private Integer deliverySequence;

    public TripOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Integer getPickupSequence() { return pickupSequence; }
    public void setPickupSequence(Integer pickupSequence) { this.pickupSequence = pickupSequence; }
    public Integer getDeliverySequence() { return deliverySequence; }
    public void setDeliverySequence(Integer deliverySequence) { this.deliverySequence = deliverySequence; }
}
