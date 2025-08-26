package com.weather.weather_mvp.repository;

import com.weather.weather_mvp.entity.Location;
import com.weather.weather_mvp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByUser(User user);

    Optional<Location> findByUserAndName(User user, String name);

    Optional<Location> findByIdAndUserId(Long id, Long userId);
}
