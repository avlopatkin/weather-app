package com.weather.weather_mvp.service;

import com.weather.weather_mvp.config.OpenWeatherProperties;
import com.weather.weather_mvp.dto.GeocodingResponse;
import com.weather.weather_mvp.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenWeatherService {

    private final OpenWeatherProperties properties;
    private final RestTemplate restTemplate;

    public List<GeocodingResponse> searchLocationsByName(String name) {
        String url = UriComponentsBuilder
                .fromUriString(properties.getBaseUrl())
                .path(properties.getGeocodingPath())
                .queryParam("q", name)
                .queryParam("limit", 5)
                .queryParam("appid", properties.getApiKey())
                .toUriString();

        GeocodingResponse[] response = restTemplate.getForObject(url, GeocodingResponse[].class);
        return Arrays.asList(response != null ? response : new GeocodingResponse[0]);
    }

    public WeatherResponse getWeatherByCoordinates(BigDecimal lat, BigDecimal lon) {
        String url = UriComponentsBuilder
                .fromUriString(properties.getBaseUrl())
                .path(properties.getWeatherPath())
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("units", "metric")
                .queryParam("appid", properties.getApiKey())
                .toUriString();

        return restTemplate.getForObject(url, WeatherResponse.class);
    }
}
