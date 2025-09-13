package com.weather.weather_mvp.controller;

import com.weather.weather_mvp.dto.LocationDto;
import com.weather.weather_mvp.dto.LocationResponseDto;
import com.weather.weather_mvp.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService locationService;

    private static final long MOCKED_USER_ID = 1L;

    @GetMapping
    public ResponseEntity<List<LocationDto>> getUserLocations() {
        List<LocationDto> locations = locationService.getLocationsForUser(MOCKED_USER_ID);
        return ResponseEntity.ok(locations);
    }

    @PostMapping
    public ResponseEntity<LocationResponseDto> addLocation(@RequestParam String name,
                                                           @RequestParam BigDecimal latitude,
                                                           @RequestParam BigDecimal longitude) {
        LocationResponseDto response = locationService.addLocation(MOCKED_USER_ID, name, latitude, longitude);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long locationId) {
        locationService.deleteLocation(MOCKED_USER_ID, locationId);
        return ResponseEntity.noContent().build();
    }
}
