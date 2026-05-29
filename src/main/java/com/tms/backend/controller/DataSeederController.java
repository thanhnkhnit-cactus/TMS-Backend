package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.*;
import com.tms.backend.repository.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/seed")
public class DataSeederController {

    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderRepository orderRepository;

    public DataSeederController(CustomerRepository customerRepository, DriverRepository driverRepository,
                                VehicleRepository vehicleRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    @Transactional
    public ApiResponse<?> seedData() {
        Random random = new Random();

        // 1. Seed 10 Customers
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Customer c = new Customer();
            c.setCustomerCode("KH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            c.setName("Khách hàng doanh nghiệp " + i);
            c.setPhone("0901" + String.format("%06d", random.nextInt(999999)));
            c.setEmail("khachhang" + i + "@example.com");
            c.setAddress("Địa chỉ khách hàng số " + i + ", TP HCM");
            c.setCustomerType(i % 2 == 0 ? CustomerType.OLD : CustomerType.NEW);
            c.setDebtLimit(new BigDecimal("50000000"));
            c.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            c.setIsDeleted(false);
            customers.add(customerRepository.save(c));
        }

        // 2. Seed 30 Drivers
        List<Driver> drivers = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Driver d = new Driver();
            d.setFullName("Tài xế Nguyễn Văn " + (char)('A' + (i % 26)) + i);
            d.setPhone("0902" + String.format("%06d", random.nextInt(999999)));
            d.setCccd("0" + String.format("%011d", random.nextInt(Integer.MAX_VALUE)));
            d.setLicenseNo("B2-" + String.format("%08d", random.nextInt(99999999)));
            d.setStatus(DriverStatus.AVAILABLE);
            d.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            d.setIsDeleted(false);
            drivers.add(driverRepository.save(d));
        }

        // 3. Seed 50 Vehicles
        List<Vehicle> vehicles = new ArrayList<>();
        String[] types = {"TRUCK", "VAN", "CONTAINER"};
        for (int i = 1; i <= 50; i++) {
            Vehicle v = new Vehicle();
            v.setPlateNumber("51C-" + String.format("%05d", random.nextInt(99999)));
            v.setVehicleType(types[random.nextInt(types.length)]);
            v.setCapacityKg(new BigDecimal(1000 + random.nextInt(9000)));
            v.setStatus(VehicleStatus.AVAILABLE);
            v.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            v.setIsDeleted(false);
            vehicles.add(vehicleRepository.save(v));
        }

        // 4. Seed 1000 Orders
        List<Order> orders = new ArrayList<>();
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 1; i <= 1000; i++) {
            Order o = new Order();
            o.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            o.setCustomer(customers.get(random.nextInt(customers.size())));
            o.setPickupAddress("Kho hàng số " + (1 + random.nextInt(5)) + ", TP HCM");
            o.setDeliveryAddress("Địa chỉ giao số " + i + ", Tỉnh " + (1 + random.nextInt(60)));
            o.setWeightKg(new BigDecimal(10 + random.nextInt(990)));
            
            o.setStatus(statuses[random.nextInt(statuses.length)]);
            o.setCodAmount(new BigDecimal(random.nextInt(5) * 1000000)); // 0 to 4M
            o.setPrice(new BigDecimal((1 + random.nextInt(10)) * 100000));
            o.setDescription("Đơn hàng test số " + i);
            o.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            o.setIsDeleted(false);
            
            orders.add(o);
        }
        orderRepository.saveAll(orders);

        return ApiResponse.success("Đã tạo 10 khách hàng, 30 tài xế, 50 xe và 1000 đơn hàng thành công!", null);
    }
}
