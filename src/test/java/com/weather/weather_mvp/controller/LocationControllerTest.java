package com.weather.weather_mvp.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LocationControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO users (id, login, password) VALUES (1, 'testuser', 'password')");
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE users, locations RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("GET /api/locations")
    void getUserLocations_ShouldReturnLocationList() throws Exception {

        jdbcTemplate.update("INSERT INTO locations (id, name, user_id, latitude, longitude) VALUES (1, 'Moscow', 1, 55.75, 37.61)");

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Moscow")))
                .andExpect(jsonPath("$[0].latitude", is(55.75)))
                .andExpect(jsonPath("$[0].longitude", is(37.61)))
                .andExpect(jsonPath("$[0].temperature").isEmpty());
    }

    @Test
    @DisplayName("POST /api/locations")
    void addLocation_ShouldCreateLocation() throws Exception {
        mockMvc.perform(post("/api/locations")
                        .param("name", "London")
                        .param("latitude", "51.50")
                        .param("longitude", "-0.12"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Location 'London' was successfully added.")))
                .andExpect(jsonPath("$.locationId").exists());

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM locations WHERE name = 'London'", Integer.class);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("DELETE /api/locations/{id}")
    void deleteLocation_ShouldDeleteLocation() throws Exception {
        jdbcTemplate.update("INSERT INTO locations (id, name, user_id, latitude, longitude) VALUES (100, 'Todelete', 1, 0, 0)");

        mockMvc.perform(delete("/api/locations/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Location 'Todelete' was successfully deleted.")));

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM locations WHERE id = 100", Integer.class);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("DELETE /api/locations/{id}")
    void deleteLocation_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/locations/999"))
                .andExpect(status().isNotFound());
    }
}
