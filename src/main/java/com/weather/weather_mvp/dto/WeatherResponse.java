package com.weather.weather_mvp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeatherResponse {

    @JsonProperty("main")
    private MainInfo mainInfo;

    @JsonProperty("weather")
    private List<WeatherInfo> weatherInfo;

    @JsonProperty("name")
    private String cityName;

    @Data
    public static class MainInfo {
        private BigDecimal temp;
        private BigDecimal feelsLike;
        private BigDecimal tempMin;
        private BigDecimal tempMax;
        private Integer pressure;
        private Integer humidity;
    }

    @Data
    public static class WeatherInfo {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }
}
