package com.weather.weather_mvp.repository;

import com.weather.weather_mvp.entity.Location;
import com.weather.weather_mvp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE l.user = :user")
    List<Location> findAllByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM Location l WHERE l.id = :locationId")
    int deleteByIdAndUserId(@Param("locationId") Long locationId, @Param("userId") Long userId);
}
