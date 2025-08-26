package com.weather.weather_mvp.service;

import com.weather.weather_mvp.dto.LocationDto;
import com.weather.weather_mvp.dto.LocationResponseDto;
import com.weather.weather_mvp.dto.MessageResponseDto;
import com.weather.weather_mvp.entity.Location;
import com.weather.weather_mvp.entity.User;
import com.weather.weather_mvp.exception.ResourceNotFoundException;
import com.weather.weather_mvp.repository.LocationRepository;
import com.weather.weather_mvp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LocationService locationService;

    @Test
    void getLocationsForUser_ShouldReturnLocations_WhenUserExists() {
        User testUser = new User("testuser", "password");
        testUser.setId(1L);
        Location testLocation = new Location("Moscow", new BigDecimal("55.75"), new BigDecimal("37.61"), testUser);
        testLocation.setId(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(locationRepository.findAllByUser(testUser)).thenReturn(List.of(testLocation));

        List<LocationDto> result = locationService.getLocationsForUser(1L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Moscow");
        assertThat(result.get(0).getId()).isEqualTo(100);
    }

    @Test
    void getLocationsForUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            locationService.getLocationsForUser(1L);
        });
    }

    @Test
    void addLocation_ShouldAddLocationSuccessfully() {
        User testUser = new User("testuser", "password");
        testUser.setId(1L);
        Location testLocation = new Location("Moscow", new BigDecimal("55.75"), new BigDecimal("37.61"), testUser);
        testLocation.setId(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

        LocationResponseDto response = locationService.addLocation(1L, "Moscow", new BigDecimal("55.75"), new BigDecimal("37.61"));

        assertThat(response).isNotNull();
        assertThat(response.getLocationId()).isEqualTo(100);
        assertThat(response.getMessage()).contains("successfully added");

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void deleteLocation_ShouldDeleteLocationSuccessfully() {
        User testUser = new User("testuser", "password");

        testUser.setId(1L);
        Location testLocation = new Location("Moscow", new BigDecimal("55.75"), new BigDecimal("37.61"), testUser);
        testLocation.setId(100L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(locationRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(testLocation));

        doNothing().when(locationRepository).delete(testLocation);

        MessageResponseDto response = locationService.deleteLocation(1L, 100L);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("successfully deleted");
        verify(locationRepository, times(1)).delete(testLocation);
    }

    @Test
    void deleteLocation_ShouldThrowException_WhenLocationNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);

        when(locationRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            locationService.deleteLocation(1L, 100L);
        });

        verify(locationRepository, never()).delete(any(Location.class));
    }
}
