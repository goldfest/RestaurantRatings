package com.example.repository;

import com.example.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    //способ 1: через конвенцию имени метода
    List<Restaurant> findByRatingGreaterThanEqual(BigDecimal minRating);

    //способ 2: через @Query с JPQL
    @Query("SELECT r FROM Restaurant r WHERE r.rating >= :minRating ORDER BY r.rating DESC")
    List<Restaurant> findRestaurantsWithMinRating(@Param("minRating") BigDecimal minRating);

    List<Restaurant> findByCuisineType(String cuisineType);
}