package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.dto.CreateTripRequest;
import com.tms.backend.entity.Trip;
import com.tms.backend.repository.TripRepository;
import com.tms.backend.service.TripService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    private final TripRepository tripRepository;
    private final com.tms.backend.repository.TripHistoryRepository tripHistoryRepository;
    private final com.tms.backend.repository.InvoiceRepository invoiceRepository;
    private final com.tms.backend.repository.TripOrderRepository tripOrderRepository;

    public TripController(TripService tripService, TripRepository tripRepository,
                          com.tms.backend.repository.TripHistoryRepository tripHistoryRepository,
                          com.tms.backend.repository.InvoiceRepository invoiceRepository,
                          com.tms.backend.repository.TripOrderRepository tripOrderRepository) {
        this.tripService = tripService;
        this.tripRepository = tripRepository;
        this.tripHistoryRepository = tripHistoryRepository;
        this.invoiceRepository = invoiceRepository;
        this.tripOrderRepository = tripOrderRepository;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('TRIP_READ', 'EXPENSE_CREATE', 'EXPENSE_UPDATE', 'EXPENSE_READ')")
    public ApiResponse<?> getAllTrips(
            @RequestParam(required = false, defaultValue = "false") boolean paginate,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) com.tms.backend.entity.TripStatus status,
            @org.springframework.data.web.PageableDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        if (paginate) {
            return ApiResponse.success(tripRepository.searchTrips(search, status, pageable));
        }
        // If not paginated, fetch all (using unpaged or custom method)
        // Spring Data allows Pageable.unpaged()
        return ApiResponse.success(tripRepository.searchTrips(search, status, org.springframework.data.domain.Pageable.unpaged()).getContent());
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('TRIP_CREATE')")
    public ApiResponse<Trip> createTrip(@RequestBody CreateTripRequest request) {
        return ApiResponse.success("Trip created", tripService.createTrip(request));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('TRIP_UPDATE')")
    public ApiResponse<?> updateTrip(@PathVariable Long id, @RequestBody com.tms.backend.dto.UpdateTripRequest request) {
        tripService.updateTrip(id, request);
        return ApiResponse.success("Trip updated successfully", null);
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('TRIP_DELETE')")
    public ApiResponse<?> deleteTrip(@PathVariable Long id) {
        Trip existing = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        existing.setIsDeleted(true);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        String currentUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        existing.setDeletedBy(currentUser);
        tripRepository.save(existing);
        return ApiResponse.success("Trip deleted successfully", null);
    }

    @GetMapping("/{id}/details")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('TRIP_READ')")
    public ApiResponse<com.tms.backend.dto.TripDetailsResponse> getTripDetails(@PathVariable Long id) {
        
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        java.util.List<com.tms.backend.entity.TripHistory> history = tripHistoryRepository.findByTripIdOrderByChangedAtDesc(id);
        
        java.util.List<com.tms.backend.entity.TripOrder> tripOrders = tripOrderRepository.findByTripId(id);
        java.util.List<com.tms.backend.entity.Order> orders = tripOrders.stream().map(com.tms.backend.entity.TripOrder::getOrder).collect(java.util.stream.Collectors.toList());
        java.util.List<Long> orderIds = orders.stream().map(com.tms.backend.entity.Order::getId).collect(java.util.stream.Collectors.toList());
        
        java.util.List<com.tms.backend.entity.Invoice> invoices = new java.util.ArrayList<>();
        if (!orderIds.isEmpty()) {
            invoices = invoiceRepository.findByOrderIdIn(orderIds);
        }
        
        return ApiResponse.success(new com.tms.backend.dto.TripDetailsResponse(trip, invoices, history, orders));
    }
}
