package com.weather.weather_mvp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Integer id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal temperature;
}
