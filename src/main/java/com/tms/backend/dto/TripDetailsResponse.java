package com.tms.backend.dto;

import com.tms.backend.entity.Invoice;
import com.tms.backend.entity.Trip;
import com.tms.backend.entity.TripHistory;

import java.util.List;

public class TripDetailsResponse {
    private Trip trip;
    private List<Invoice> invoices;
    private List<TripHistory> history;
    private List<com.tms.backend.entity.Order> orders;

    public TripDetailsResponse(Trip trip, List<Invoice> invoices, List<TripHistory> history, List<com.tms.backend.entity.Order> orders) {
        this.trip = trip;
        this.invoices = invoices;
        this.history = history;
        this.orders = orders;
    }

    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public List<Invoice> getInvoices() { return invoices; }
    public void setInvoices(List<Invoice> invoices) { this.invoices = invoices; }
    public List<TripHistory> getHistory() { return history; }
    public void setHistory(List<TripHistory> history) { this.history = history; }
    public List<com.tms.backend.entity.Order> getOrders() { return orders; }
    public void setOrders(List<com.tms.backend.entity.Order> orders) { this.orders = orders; }
}
