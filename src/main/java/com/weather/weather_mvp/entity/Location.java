package com.weather.weather_mvp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "locations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Location(String name, BigDecimal latitude, BigDecimal longitude, User user) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }
}
