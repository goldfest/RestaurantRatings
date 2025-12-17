package com.example.repository;

import com.example.AbstractIntegrationTest;
import com.example.entity.Restaurant;
import com.example.entity.CuisineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class RestaurantRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
    }

    @Test
    void saveAndFindById_WorksCorrectly() {
        Restaurant restaurant = createRestaurant("Test Restaurant", new BigDecimal("4.5"));
        Restaurant saved = restaurantRepository.save(restaurant);

        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Restaurant", found.get().getName());
        assertEquals(new BigDecimal("4.5"), found.get().getRating());
    }

    @Test
    void findByRatingGreaterThanEqual_FiltersCorrectly() {
        Restaurant r1 = createRestaurant("High Rated", new BigDecimal("4.8"));
        Restaurant r2 = createRestaurant("Medium Rated", new BigDecimal("3.9"));
        Restaurant r3 = createRestaurant("Low Rated", new BigDecimal("2.5"));

        restaurantRepository.saveAll(List.of(r1, r2, r3));

        List<Restaurant> highRated = restaurantRepository
                .findByRatingGreaterThanEqual(new BigDecimal("4.0"));
        List<Restaurant> allRated = restaurantRepository
                .findByRatingGreaterThanEqual(new BigDecimal("0.0"));

        assertEquals(1, highRated.size());
        assertEquals(3, allRated.size());
    }

    @Test
    void findRestaurantsWithMinRating_JpqlQueryWorks() {
        Restaurant r1 = createRestaurant("Restaurant 1", new BigDecimal("4.2"));
        Restaurant r2 = createRestaurant("Restaurant 2", new BigDecimal("3.9"));

        restaurantRepository.saveAll(List.of(r1, r2));

        List<Restaurant> result = restaurantRepository
                .findRestaurantsWithMinRating(new BigDecimal("4.0"));

        assertEquals(1, result.size());
        assertEquals("Restaurant 1", result.get(0).getName());
    }

    private Restaurant createRestaurant(String name, BigDecimal rating) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setDescription("Description for " + name);
        restaurant.setCuisineType(CuisineType.ITALIAN);
        restaurant.setAverageBill(new BigDecimal("1000.00"));
        restaurant.setRating(rating);
        return restaurant;
    }
}