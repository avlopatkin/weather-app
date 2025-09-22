package com.weather.weather_mvp.controller;

import com.weather.weather_mvp.dto.LoginRequest;
import com.weather.weather_mvp.dto.UserRegistrationRequest;
import com.weather.weather_mvp.dto.UserResponseDto;
import com.weather.weather_mvp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationRequest request) {
        UserResponseDto response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody LoginRequest request) {
        UserResponseDto response = authService.login(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth endpoint works!");
    }
}
