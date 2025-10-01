package com.weather.weather_mvp.service;


import com.weather.weather_mvp.dto.LoginResponseDto;
import com.weather.weather_mvp.dto.UserRegistrationRequest;
import com.weather.weather_mvp.dto.UserResponseDto;
import com.weather.weather_mvp.entity.User;
import com.weather.weather_mvp.repository.UserRepository;
import com.weather.weather_mvp.security.UserDetailsAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserResponseDto register(UserRegistrationRequest request) {
        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("User already exists with login: " + request.getLogin());
        }

        String email = request.getLogin() + "@example.com";

        User user = new User(
                request.getLogin(),
                email,
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        return UserResponseDto.builder()
                .message("User registered successfully")
                .userId(Math.toIntExact(savedUser.getId()))
                .login(savedUser.getLogin())
                .build();
    }

    public LoginResponseDto login(String login, String password) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), password)
        );

        UserDetailsAdapter userDetails = new UserDetailsAdapter(user);
        String token = jwtService.generateToken(userDetails);

        return LoginResponseDto.builder()
                .message("Login successful")
                .userId(Math.toIntExact(user.getId()))
                .login(user.getLogin())
                .token(token)
                .build();
    }
}
