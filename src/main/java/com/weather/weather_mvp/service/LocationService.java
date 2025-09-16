package com.weather.weather_mvp.service;

import com.weather.weather_mvp.dto.GeocodingResponse;
import com.weather.weather_mvp.dto.LocationDto;
import com.weather.weather_mvp.dto.LocationResponseDto;
import com.weather.weather_mvp.entity.Location;
import com.weather.weather_mvp.entity.User;
import com.weather.weather_mvp.exception.ResourceNotFoundException;
import com.weather.weather_mvp.repository.LocationRepository;
import com.weather.weather_mvp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final OpenWeatherService openWeatherService;

    @Cacheable(value = "locations", key = "#userId")
    @Transactional(readOnly = true)
    public List<LocationDto> getLocationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Location> locations = locationRepository.findAllByUser(user);

        return locations.stream()
                .map(this::mapToLocationDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "locations", key = "#userId")
    @Transactional
    public LocationResponseDto addLocation(Long userId, String name, BigDecimal latitude, BigDecimal longitude) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Location newLocation = new Location(name, latitude, longitude, user);

        Location savedLocation = locationRepository.save(newLocation);

        return LocationResponseDto.builder()
                .message("Location '" + savedLocation.getName() + "' was successfully added.")
                .locationId(savedLocation.getId().intValue())
                .build();
    }

    @Transactional
    @CacheEvict(value = "locations", key = "#userId")
    public void deleteLocation(Long userId, Long locationId) {
        int deletedRows = locationRepository.deleteByIdAndUserId(locationId, userId);

        if (deletedRows == 0) {
            throw new ResourceNotFoundException("Location not found with id: " + locationId + " for user: " + userId);
        }
    }

    private LocationDto mapToLocationDto(Location location) {
        BigDecimal temperature = openWeatherService
                .getWeatherByCoordinates(location.getLatitude(), location.getLongitude())
                .getMainInfo()
                .getTemp();

        return LocationDto.builder()
                .id(location.getId().intValue())
                .name(location.getName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .temperature(temperature)
                .build();
    }

    public List<GeocodingResponse> searchLocationsByName(String name) {
        return openWeatherService.searchLocationsByName(name);
    }
}
