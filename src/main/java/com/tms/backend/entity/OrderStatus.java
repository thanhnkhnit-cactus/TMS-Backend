package com.tms.backend.entity;

public enum OrderStatus {
    PENDING,
    NEW,
    CONFIRMED,
    DISPATCHED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    FAILED
}
