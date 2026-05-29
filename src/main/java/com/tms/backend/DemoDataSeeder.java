package com.tms.backend;

import com.tms.backend.entity.*;
import com.tms.backend.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoDataSeeder {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public DemoDataSeeder(DriverRepository driverRepository,
                      VehicleRepository vehicleRepository,
                      CustomerRepository customerRepository,
                      OrderRepository orderRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/api/test/seed")
    @Transactional
    public String runSeed() {
        Random random = new Random();

        // 1. Create 10 Drivers
        String[] firstNames = {"Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng"};
        String[] lastNames = {"Anh", "Hùng", "Dũng", "Minh", "Thành", "Công", "Bình", "Sơn", "Hải", "Tuấn"};
        for (int i = 0; i < 10; i++) {
            Driver driver = new Driver();
            String name = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
            driver.setFullName(name);
            driver.setPhone("09" + (10000000 + random.nextInt(90000000)));
            driver.setLicenseNo("B2-" + (100000 + random.nextInt(900000)));
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            driverRepository.save(driver);
        }

        // 2. Create 30 Vehicles
        String[] types = {"Xe Tải 1.5 Tấn", "Xe Tải 2.5 Tấn", "Xe Bán Tải", "Xe Tải 5 Tấn"};
        for (int i = 0; i < 30; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setPlateNumber(String.format("29C-%05d", 10000 + random.nextInt(90000)));
            vehicle.setVehicleType(types[random.nextInt(types.length)]);
            vehicle.setCapacityKg(BigDecimal.valueOf(1000 + random.nextInt(4000)));
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicle.setInspectionExpiry(java.time.LocalDate.now().plusMonths(6 + random.nextInt(12)));
            vehicle.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
            vehicleRepository.save(vehicle);
        }

        // 3. Create 100 Orders
        Customer customer;
        if (customerRepository.count() == 0) {
            customer = new Customer();
            customer.setName("Công ty TNHH Vận tải Thử Nghiệm");
            customer.setPhone("02431234567");
            customer.setAddress("KCN Thăng Long, Đông Anh, Hà Nội");
            customer.setCreatedAt(LocalDateTime.now());
            customer = customerRepository.save(customer);
        } else {
            customer = customerRepository.findAll().get(0);
        }

        String[] addresses = {
            "Quận 1, TP. HCM", "Quận Đống Đa, Hà Nội", "Hải An, Hải Phòng", 
            "Cẩm Lệ, Đà Nẵng", "Ninh Kiều, Cần Thơ", "Thủ Dầu Một, Bình Dương",
            "Biên Hòa, Đồng Nai", "Hạ Long, Quảng Ninh", "Thanh Xuân, Hà Nội"
        };
        
        String[] goods = {"Thiết bị điện tử", "Quần áo may mặc", "Thực phẩm đông lạnh", "Vật liệu xây dựng", "Nội thất văn phòng", "Dược phẩm", "Mỹ phẩm", "Linh kiện ô tô"};

        for (int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            order.setCustomer(customer);
            order.setCustomerCode("CUST-001");
            order.setCustomerName(customer.getName());
            order.setCustomerPhone(customer.getPhone());
            order.setCustomerAddress(customer.getAddress());
            
            order.setPickupAddress(addresses[random.nextInt(addresses.length)]);
            order.setDeliveryAddress(addresses[random.nextInt(addresses.length)]);
            order.setGoodsName(goods[random.nextInt(goods.length)]);
            order.setWeightKg(BigDecimal.valueOf(10 + random.nextInt(500)));
            
            // Price: 100k - 5M
            order.setPrice(BigDecimal.valueOf((100 + random.nextInt(4900)) * 1000));
            
            // 30% chance of having COD
            if (random.nextInt(100) < 30) {
                order.setCodAmount(BigDecimal.valueOf((50 + random.nextInt(2000)) * 1000));
            } else {
                order.setCodAmount(BigDecimal.ZERO);
            }
            
            order.setStatus(OrderStatus.NEW);
            order.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(10)));
            
            orderRepository.save(order);
        }
        
        return "Tạo thành công 10 tài xế, 30 xe, 100 đơn hàng!";
    }
}
