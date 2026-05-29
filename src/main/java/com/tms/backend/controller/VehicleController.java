package com.tms.backend.controller;

import com.tms.backend.dto.ApiResponse;
import com.tms.backend.entity.Vehicle;
import com.tms.backend.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyAuthority('VEHICLE_READ', 'TRIP_CREATE', 'TRIP_UPDATE', 'TRIP_READ')")
    public ApiResponse<?> getAllVehicles(
            @RequestParam(required = false, defaultValue = "false") boolean paginate,
            @RequestParam(required = false) com.tms.backend.entity.VehicleStatus status,
            @RequestParam(required = false, defaultValue = "") String search,
            @org.springframework.data.web.PageableDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        if (paginate) {
            return ApiResponse.success(vehicleService.getVehiclesPage(search, status, pageable));
        }
        return ApiResponse.success(vehicleService.getAllVehicles());
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('VEHICLE_CREATE')")
    public ApiResponse<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ApiResponse.success("Vehicle created", vehicleService.createVehicle(vehicle));
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('VEHICLE_UPDATE')")
    public ApiResponse<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return ApiResponse.success("Vehicle updated", vehicleService.updateVehicle(id, vehicle));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('VEHICLE_DELETE')")
    public ApiResponse<?> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ApiResponse.success("Vehicle deleted", null);
    }
}
