package com.example;

import com.example.entity.CuisineType;
import com.example.entity.Restaurant;
import com.example.entity.Review;
import com.example.entity.Visitor;
import com.example.entity.*;
import com.example.service.RestaurantService;
import com.example.service.ReviewService;
import com.example.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@RequiredArgsConstructor
public class RestaurantRatingApplication {
    private final VisitorService visitorService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    public static void main(String[] args) {
        SpringApplication.run(RestaurantRatingApplication.class, args);
    }

    @Bean
    public CommandLineRunner testApplication() {
        return args -> {
            Visitor visitor1 = new Visitor(null, "Ivan", 25, "Man");
            Visitor visitor2 = new Visitor(null, "Stepan", 30, "Woman");
            Visitor visitor3 = new Visitor(null, null, 28, "Man"); //анонимный отзыв

            visitorService.save(visitor1);
            visitorService.save(visitor2);
            visitorService.save(visitor3);

            // Создание ресторанов
            Restaurant restaurant1 = new Restaurant(null, "Italiano pastanello",
                    "italian kukaracha", CuisineType.ITALIAN,
                    new BigDecimal("1500.00"), BigDecimal.ZERO);

            Restaurant restaurant2 = new Restaurant(null, "Doshiraki",
                    "bla bla bla", CuisineType.CHINESE,
                    new BigDecimal("1200.00"), BigDecimal.ZERO);

            Restaurant restaurant3 = new Restaurant(null, "Baget",
                    "fransish", CuisineType.FRENCH,
                    new BigDecimal("2500.00"), BigDecimal.ZERO);

            restaurantService.save(restaurant1);
            restaurantService.save(restaurant2);
            restaurantService.save(restaurant3);

            Review review1 = new Review(null, visitor1.getId(), restaurant1.getId(),
                    5, "Vai vai vai");
            Review review2 = new Review(null, visitor2.getId(), restaurant1.getId(),
                    4, "555555");
            Review review3 = new Review(null, visitor3.getId(), restaurant2.getId(),
                    3, "Normal");
            Review review4 = new Review(null, visitor1.getId(), restaurant2.getId(),
                    5, "nyam nyam");
            Review review5 = new Review(null, visitor2.getId(), restaurant3.getId(),
                    5, "Prosto prelest");

            reviewService.save(review1);
            reviewService.save(review2);
            reviewService.save(review3);
            reviewService.save(review4);
            reviewService.save(review5);

            // Тестирование и вывод результатов
            System.out.println("TEST");

            System.out.println("\nVisitors");
            visitorService.findAll().forEach(System.out::println);

            System.out.println("\nRestaurant");
            restaurantService.findAll().forEach(restaurant ->
                    System.out.println(restaurant.getName() + " - rating: " + restaurant.getRating()));

            System.out.println("\nReviews");
            reviewService.findAll().forEach(System.out::println);
        };
    }
}