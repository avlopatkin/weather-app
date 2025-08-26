package com.weather.weather_mvp.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String login;
    private String password;
}
