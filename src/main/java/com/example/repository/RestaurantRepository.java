package com.example.repository;

import com.example.entity.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RestaurantRepository {
    private final List<Restaurant> restaurants = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Restaurant save(Restaurant restaurant) {
        if (restaurant.getId() == null) {
            restaurant.setId(idCounter.getAndIncrement());
            restaurants.add(restaurant);
            return restaurant;
        } else {
            Optional<Restaurant> existingRestaurant = findById(restaurant.getId());
            if (existingRestaurant.isPresent()) {
                restaurants.remove(existingRestaurant.get());
                restaurants.add(restaurant);
                return restaurant;
            }
            return null;
        }
    }

    public boolean remove(Long id) {
        return restaurants.removeIf(restaurant -> restaurant.getId().equals(id));
    }

    public List<Restaurant> findAll() {
        return new ArrayList<>(restaurants);
    }

    public Optional<Restaurant> findById(Long id) {
        return restaurants.stream()
                .filter(restaurant -> restaurant.getId().equals(id))
                .findFirst();
    }
}