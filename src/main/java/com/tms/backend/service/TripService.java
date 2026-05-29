package com.tms.backend.service;

import com.tms.backend.dto.CreateTripRequest;
import com.tms.backend.entity.Trip;

public interface TripService {
    Trip createTrip(CreateTripRequest request);
    void updateTrip(Long id, com.tms.backend.dto.UpdateTripRequest request);
}
