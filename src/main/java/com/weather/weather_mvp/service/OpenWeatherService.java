package com.weather.weather_mvp.service;

import com.weather.weather_mvp.config.OpenWeatherProperties;
import com.weather.weather_mvp.dto.GeocodingResponse;
import com.weather.weather_mvp.dto.WeatherResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenWeatherService {
    private final OpenWeatherProperties properties;
    private WebClient webClient;

    @PostConstruct
    private void init() {
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public List<GeocodingResponse> searchLocationsByName(String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getGeocodingPath())
                        .queryParam("q", name)
                        .queryParam("limit", 5)
                        .queryParam("appid", properties.getApiKey())
                        .build())
                .retrieve()
                .bodyToFlux(GeocodingResponse.class)
                .collectList()
                .block();
    }

    public WeatherResponse getWeatherByCoordinates(BigDecimal lat, BigDecimal lon) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(properties.getWeatherPath())
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("units", "metric")
                        .queryParam("appid", properties.getApiKey())
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .block();
    }
}
