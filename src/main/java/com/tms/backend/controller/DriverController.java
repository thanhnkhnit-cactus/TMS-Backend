package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.Driver;
import com.tms.backend.service.DriverService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('DRIVER_READ', 'TRIP_CREATE', 'TRIP_UPDATE', 'TRIP_READ')")
    public ApiResponse<?> getAllDrivers(
            @RequestParam(required = false, defaultValue = "false") boolean paginate,
            @RequestParam(required = false) com.tms.backend.entity.DriverStatus status,
            @RequestParam(required = false, defaultValue = "") String search,
            @org.springframework.data.web.PageableDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        if (paginate) {
            return ApiResponse.success(driverService.getDriversPage(search, status, pageable));
        }
        return ApiResponse.success(driverService.getAllDrivers());
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('DRIVER_CREATE')")
    public ApiResponse<Driver> createDriver(@RequestBody Driver driver) {
        return ApiResponse.success("Driver created", driverService.createDriver(driver));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('DRIVER_UPDATE')")
    public ApiResponse<Driver> updateDriver(@PathVariable Long id, @RequestBody Driver driver) {
        return ApiResponse.success("Driver updated", driverService.updateDriver(id, driver));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('DRIVER_DELETE')")
    public ApiResponse<?> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ApiResponse.success("Driver deleted", null);
    }
}
