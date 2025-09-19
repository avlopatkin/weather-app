package com.weather.weather_mvp.service;

import com.weather.weather_mvp.dto.GeocodingResponse;
import com.weather.weather_mvp.dto.LocationDto;
import com.weather.weather_mvp.dto.LocationResponseDto;
import com.weather.weather_mvp.entity.Location;
import com.weather.weather_mvp.entity.User;
import com.weather.weather_mvp.exception.ResourceNotFoundException;
import com.weather.weather_mvp.repository.LocationRepository;
import com.weather.weather_mvp.repository.UserRepository;
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
class LocationServiceTest {
    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OpenWeatherService openWeatherService;

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

        assertThrows(ResourceNotFoundException.class, () -> locationService.getLocationsForUser(1L));
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
        assertThat(response.getMessage()).contains("successfully added");
        assertThat(response.getLocationId()).isEqualTo(100);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void deleteLocation_ShouldDeleteSuccessfully() {
        long userId = 1L;
        long locationId = 100L;
        when(locationRepository.deleteByIdAndUserId(locationId)).thenReturn(1);

        locationService.deleteLocation(userId, locationId);

        verify(locationRepository, times(1)).deleteByIdAndUserId(locationId);
    }

    @Test
    void deleteLocation_ShouldThrowException_WhenLocationNotFound() {
        long userId = 1L;
        long locationId = 999L;
        when(locationRepository.deleteByIdAndUserId(locationId)).thenReturn(0);

        assertThrows(ResourceNotFoundException.class, () -> locationService.deleteLocation(userId, locationId));

        verify(locationRepository, times(1)).deleteByIdAndUserId(locationId);
    }

    @Test
    void searchLocationsByName_ShouldReturnGeocodingResponse() {
        GeocodingResponse geocodingResponse = new GeocodingResponse();
        geocodingResponse.setName("Paris");
        geocodingResponse.setCountry("FR");

        when(openWeatherService.searchLocationsByName("Paris"))
                .thenReturn(List.of(geocodingResponse));

        List<GeocodingResponse> result = locationService.searchLocationsByName("Paris");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Paris");
        assertThat(result.get(0).getCountry()).isEqualTo("FR");
    }
}
