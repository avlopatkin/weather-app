package com.weather.weather_mvp.service;

import com.weather.weather_mvp.dto.LocationDto;
import com.weather.weather_mvp.dto.LocationResponseDto;
import com.weather.weather_mvp.dto.MessageResponseDto;
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
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    private static final long MOCKED_USER_ID = 1L;

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

    @CacheEvict(value = "locations", key = "#userId")
    @Transactional
    public MessageResponseDto deleteLocation(Long userId, Long locationId) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Location locationToDelete = locationRepository.findByIdAndUserId(locationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId + " for user " + userId));

        locationRepository.delete(locationToDelete);

        return MessageResponseDto.builder()
                .message("Location '" + locationToDelete.getName() + "' was successfully deleted.")
                .build();
    }

    private LocationDto mapToLocationDto(Location location) {
        return LocationDto.builder()
                .id(location.getId().intValue())
                .name(location.getName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                // TODO: Имплементировать получение реальной температуры, когда будет готов OpenWeather API
                .temperature(null)
                .build();
    }

}
