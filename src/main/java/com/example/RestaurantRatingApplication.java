package com.example;

import com.example.dto.RestaurantResponseDTO;
import com.example.dto.ReviewRequestDTO;
import com.example.dto.ReviewResponseDTO;
import com.example.dto.VisitorRequestDTO;

import com.example.dto.RestaurantRequestDTO;
import com.example.dto.VisitorResponseDTO;
import com.example.entity.CuisineType;
import com.example.service.RestaurantService;
import com.example.service.ReviewService;
import com.example.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class RestaurantRatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantRatingApplication.class, args);
    }

    @Bean
    public CommandLineRunner testApplication(
            VisitorService visitorService,
            RestaurantService restaurantService,
            ReviewService reviewService) {

        return args -> {
            System.out.println("Application started successfully!");
            System.out.println("Swagger UI available at: http://localhost:8080/swagger-ui.html");

            VisitorRequestDTO visitor1 = new VisitorRequestDTO("Ivan", 25, "Man");
            VisitorRequestDTO visitor2 = new VisitorRequestDTO("Anna", 30, "Woman");

            VisitorResponseDTO savedVisitor1 = visitorService.save(visitor1);
            VisitorResponseDTO savedVisitor2 = visitorService.save(visitor2);

            RestaurantRequestDTO restaurant1 = new RestaurantRequestDTO(
                    "Italian Paradise",
                    "Authentic Italian cuisine",
                    CuisineType.ITALIAN,
                    new BigDecimal("1500.00")
            );

            RestaurantResponseDTO savedRestaurant1 = restaurantService.save(restaurant1);

            ReviewRequestDTO review1 = new ReviewRequestDTO(
                    savedVisitor1.id(),
                    savedRestaurant1.id(),
                    5,
                    "беллисимо"
            );

            reviewService.save(review1);

            Page<ReviewResponseDTO> reviewsPage = reviewService.getReviewsByRestaurant(
                    savedRestaurant1.id(), 0, 10, "rating", "desc");
            System.out.println("все отзывы: " + reviewsPage.getTotalElements());

            List<RestaurantResponseDTO> highRatedRestaurants = restaurantService
                    .findRestaurantsWithMinRating(new BigDecimal("4.0"));
            System.out.println("Рестораны с высокой оценкой: " + highRatedRestaurants.size());
        };
    }
}