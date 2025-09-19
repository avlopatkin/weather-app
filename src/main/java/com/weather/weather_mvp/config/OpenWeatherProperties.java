package com.weather.weather_mvp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openweather")
public class OpenWeatherProperties {
    private String apiKey;
    private String baseUrl;
    private String geocodingPath;
    private String weatherPath;
}
