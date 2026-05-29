package com.tms.backend.service;

import com.tms.backend.dto.CreateTripRequest;
import com.tms.backend.entity.*;
import com.tms.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final OrderRepository orderRepository;
    private final TripOrderRepository tripOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final TripHistoryRepository tripHistoryRepository;
    private final ExpenseRepository expenseRepository;

    public TripServiceImpl(TripRepository tripRepository, VehicleRepository vehicleRepository,
                           DriverRepository driverRepository, OrderRepository orderRepository,
                           TripOrderRepository tripOrderRepository, InvoiceRepository invoiceRepository,
                           TripHistoryRepository tripHistoryRepository, ExpenseRepository expenseRepository) {
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.orderRepository = orderRepository;
        this.tripOrderRepository = tripOrderRepository;
        this.invoiceRepository = invoiceRepository;
        this.tripHistoryRepository = tripHistoryRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public Trip createTrip(CreateTripRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
                
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available");
        }
        
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available");
        }

        List<Order> orders = orderRepository.findAllById(request.getOrderIds());
        
        BigDecimal totalWeight = orders.stream()
                .map(o -> o.getWeightKg() != null ? o.getWeightKg() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        if (totalWeight.compareTo(vehicle.getCapacityKg()) > 0) {
            throw new RuntimeException("Total weight exceeds vehicle capacity");
        }

        Trip trip = new Trip();
        trip.setTripCode("TRIP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setStatus(TripStatus.PLANNED);
        trip.setCreatedAt(LocalDateTime.now());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setEstimatedArrivalTime(request.getEstimatedArrivalTime());
        trip = tripRepository.save(trip);

        int seq = 1;
        for (Order order : orders) {
            TripOrder to = new TripOrder();
            to.setTrip(trip);
            to.setOrder(order);
            to.setPickupSequence(seq);
            to.setDeliverySequence(seq);
            tripOrderRepository.save(to);
            
            if (request.getOrderUpdates() != null && request.getOrderUpdates().containsKey(order.getId())) {
                CreateTripRequest.OrderUpdateDto updateDto = request.getOrderUpdates().get(order.getId());
                if (updateDto.getCodAmount() != null) order.setCodAmount(updateDto.getCodAmount());
                if (updateDto.getDistanceKm() != null) order.setDistanceKm(updateDto.getDistanceKm());
                if (updateDto.getSurcharge() != null) order.setSurcharge(updateDto.getSurcharge());
            }
            order.setStatus(OrderStatus.DISPATCHED);
            orderRepository.save(order);
            seq++;
        }

        return trip;
    }

    @Transactional
    public void dispatchTrip(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(TripStatus.RUNNING);
        trip.setStartTime(LocalDateTime.now());
        
        Vehicle vehicle = trip.getVehicle();
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.IN_USE);
            vehicleRepository.save(vehicle);
        }
        
        Driver driver = trip.getDriver();
        if (driver != null) {
            driver.setStatus(DriverStatus.ON_TRIP);
            driverRepository.save(driver);
        }
        
        tripRepository.save(trip);
    }
    
    @Transactional
    public void completeTrip(Long id, Boolean generateInvoice, LocalDateTime endTime, BigDecimal fuelExpense, BigDecimal tollExpense) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(endTime != null ? endTime : LocalDateTime.now());
        
        Vehicle vehicle = trip.getVehicle();
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }
        
        Driver driver = trip.getDriver();
        if (driver != null) {
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }
        
        List<TripOrder> tripOrders = tripOrderRepository.findByTripId(id);
        for (TripOrder to : tripOrders) {
            Order order = to.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);

            boolean hasCod = order.getCodAmount() != null && order.getCodAmount().compareTo(BigDecimal.ZERO) > 0;
            boolean hasPrice = order.getPrice() != null && order.getPrice().compareTo(BigDecimal.ZERO) > 0;

            if (Boolean.TRUE.equals(generateInvoice)) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceCode("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                invoice.setOrder(order);
                
                BigDecimal total = BigDecimal.ZERO;
                if (order.getCodAmount() != null) total = total.add(order.getCodAmount());
                if (order.getSurcharge() != null) total = total.add(order.getSurcharge());
                
                invoice.setTotalAmount(total);
                invoice.setPaidAmount(BigDecimal.ZERO);
                invoice.setStatus("UNPAID");
                invoice.setDueDate(LocalDateTime.now().plusDays(7));
                invoice.setCreatedAt(LocalDateTime.now());
                invoiceRepository.save(invoice);
            }
        }
        
        if (fuelExpense != null && fuelExpense.compareTo(BigDecimal.ZERO) > 0) {
            Expense fuel = new Expense();
            fuel.setTrip(trip);
            fuel.setVehicle(trip.getVehicle());
            fuel.setExpenseType("FUEL");
            fuel.setAmount(fuelExpense);
            fuel.setNotes("Tự động tạo khi hoàn thành chuyến " + trip.getTripCode());
            fuel.setCreatedAt(LocalDateTime.now());
            expenseRepository.save(fuel);
        }

        if (tollExpense != null && tollExpense.compareTo(BigDecimal.ZERO) > 0) {
            Expense toll = new Expense();
            toll.setTrip(trip);
            toll.setVehicle(trip.getVehicle());
            toll.setExpenseType("TOLL");
            toll.setAmount(tollExpense);
            toll.setNotes("Tự động tạo khi hoàn thành chuyến " + trip.getTripCode());
            toll.setCreatedAt(LocalDateTime.now());
            expenseRepository.save(toll);
        }
        
        tripRepository.save(trip);
    }

    private void cancelTrip(Long id, String note) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
        trip.setStatus(TripStatus.CANCELLED);
        trip.setEndTime(LocalDateTime.now());
        if (note != null && !note.isEmpty()) {
            trip.setNote(note);
        }
        
        Vehicle vehicle = trip.getVehicle();
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }
        
        Driver driver = trip.getDriver();
        if (driver != null) {
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
        }
        
        List<TripOrder> tripOrders = tripOrderRepository.findByTripId(id);
        for (TripOrder to : tripOrders) {
            Order order = to.getOrder();
            order.setStatus(OrderStatus.NEW);
            orderRepository.save(order);
        }
        
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void updateTrip(Long id, com.tms.backend.dto.UpdateTripRequest request) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));

        if (trip.getStatus() == TripStatus.COMPLETED) {
            throw new RuntimeException("Không thể sửa đổi chuyến xe đã hoàn thành!");
        }

        if (request.getDepartureTime() != null) {
            trip.setDepartureTime(request.getDepartureTime());
        }
        if (request.getEstimatedArrivalTime() != null) {
            trip.setEstimatedArrivalTime(request.getEstimatedArrivalTime());
        }

        if (request.getVehicleId() != null && (trip.getVehicle() == null || !request.getVehicleId().equals(trip.getVehicle().getId()))) {
            Vehicle newVehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            if (newVehicle.getStatus() != VehicleStatus.AVAILABLE) {
                throw new RuntimeException("New vehicle is not available");
            }
            if (trip.getStatus() == TripStatus.RUNNING || trip.getStatus() == TripStatus.PLANNED) {
                Vehicle oldVehicle = trip.getVehicle();
                if (oldVehicle != null) {
                    oldVehicle.setStatus(VehicleStatus.AVAILABLE);
                    vehicleRepository.save(oldVehicle);
                }
                newVehicle.setStatus(trip.getStatus() == TripStatus.RUNNING ? VehicleStatus.IN_USE : VehicleStatus.AVAILABLE);
                vehicleRepository.save(newVehicle);
            }
            
            TripHistory history = new TripHistory();
            history.setTrip(trip);
            history.setFieldName("VEHICLE");
            history.setOldValueId(trip.getVehicle() != null ? trip.getVehicle().getId() : null);
            history.setOldValueName(trip.getVehicle() != null ? trip.getVehicle().getPlateNumber() : "N/A");
            history.setNewValueId(newVehicle.getId());
            history.setNewValueName(newVehicle.getPlateNumber());
            history.setChangedAt(LocalDateTime.now());
            tripHistoryRepository.save(history);

            trip.setVehicle(newVehicle);
        }

        if (request.getDriverId() != null && (trip.getDriver() == null || !request.getDriverId().equals(trip.getDriver().getId()))) {
            Driver newDriver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            if (newDriver.getStatus() != DriverStatus.AVAILABLE) {
                throw new RuntimeException("New driver is not available");
            }
            if (trip.getStatus() == TripStatus.RUNNING || trip.getStatus() == TripStatus.PLANNED) {
                Driver oldDriver = trip.getDriver();
                if (oldDriver != null) {
                    oldDriver.setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(oldDriver);
                }
                newDriver.setStatus(trip.getStatus() == TripStatus.RUNNING ? DriverStatus.ON_TRIP : DriverStatus.AVAILABLE);
                driverRepository.save(newDriver);
            }
            
            TripHistory history = new TripHistory();
            history.setTrip(trip);
            history.setFieldName("DRIVER");
            history.setOldValueId(trip.getDriver() != null ? trip.getDriver().getId() : null);
            history.setOldValueName(trip.getDriver() != null ? trip.getDriver().getFullName() : "N/A");
            history.setNewValueId(newDriver.getId());
            history.setNewValueName(newDriver.getFullName());
            history.setChangedAt(LocalDateTime.now());
            tripHistoryRepository.save(history);

            trip.setDriver(newDriver);
        }

        if (request.getOrderUpdates() != null && !request.getOrderUpdates().isEmpty()) {
            List<TripOrder> tripOrders = tripOrderRepository.findByTripId(id);
            for (TripOrder to : tripOrders) {
                Order order = to.getOrder();
                if (request.getOrderUpdates().containsKey(order.getId())) {
                    CreateTripRequest.OrderUpdateDto updateDto = request.getOrderUpdates().get(order.getId());
                    if (updateDto.getCodAmount() != null) order.setCodAmount(updateDto.getCodAmount());
                    if (updateDto.getDistanceKm() != null) order.setDistanceKm(updateDto.getDistanceKm());
                    if (updateDto.getSurcharge() != null) order.setSurcharge(updateDto.getSurcharge());
                    orderRepository.save(order);
                }
            }
        }

        if (request.getRemovedOrderIds() != null && !request.getRemovedOrderIds().isEmpty()) {
            for (Long removedOrderId : request.getRemovedOrderIds()) {
                java.util.Optional<TripOrder> optTo = tripOrderRepository.findByTripIdAndOrderId(id, removedOrderId);
                if (optTo.isPresent()) {
                    TripOrder to = optTo.get();
                    tripOrderRepository.delete(to);
                    Order order = to.getOrder();
                    order.setStatus(OrderStatus.NEW);
                    orderRepository.save(order);
                }
            }
        }

        if (request.getStatus() != null && request.getStatus() != trip.getStatus()) {
            if (request.getStatus() == TripStatus.COMPLETED) {
                completeTrip(id, request.getGenerateInvoice(), request.getEndTime(), request.getFuelExpense(), request.getTollExpense());
                return;
            } else if (request.getStatus() == TripStatus.CANCELLED) {
                cancelTrip(id, request.getNote());
                return;
            } else if (request.getStatus() == TripStatus.RUNNING) {
                dispatchTrip(id);
                return;
            }
            trip.setStatus(request.getStatus());
        }

        if (request.getNote() != null) {
            trip.setNote(request.getNote());
        }

        tripRepository.save(trip);
    }
}
