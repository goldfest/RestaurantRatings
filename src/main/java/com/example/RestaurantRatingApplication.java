package com.example;

import com.example.dto.Visitor.VisitorRequestDTO;

import com.example.dto.Restaurant.RestaurantRequestDTO;
import com.example.entity.CuisineType;
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

    public static void main(String[] args) {
        SpringApplication.run(RestaurantRatingApplication.class, args);
    }

    @Bean
    public CommandLineRunner testApplication(
            VisitorService visitorService,
            RestaurantService restaurantService,
            ReviewService reviewService) {

        return args -> {
            // Создание посетителей через DTO
            VisitorRequestDTO visitor1 = new VisitorRequestDTO("Ivan", 25, "Man");
            VisitorRequestDTO visitor2 = new VisitorRequestDTO("Stepan", 30, "Woman");
            VisitorRequestDTO visitor3 = new VisitorRequestDTO("Anonymous", 28, "Man");

            visitorService.save(visitor1);
            visitorService.save(visitor2);
            visitorService.save(visitor3);

            // Создание ресторанов через DTO
            RestaurantRequestDTO restaurant1 = new RestaurantRequestDTO(
                    "Italiano pastanello",
                    "italian kukaracha",
                    CuisineType.ITALIAN,
                    new BigDecimal("1500.00")
            );

            RestaurantRequestDTO restaurant2 = new RestaurantRequestDTO(
                    "Doshiraki",
                    "bla bla bla",
                    CuisineType.CHINESE,
                    new BigDecimal("1200.00")
            );

            RestaurantRequestDTO restaurant3 = new RestaurantRequestDTO(
                    "Baget",
                    "fransish",
                    CuisineType.FRENCH,
                    new BigDecimal("2500.00")
            );

            restaurantService.save(restaurant1);
            restaurantService.save(restaurant2);
            restaurantService.save(restaurant3);

            // Тестирование и вывод результатов
            System.out.println("Application started successfully!");
            System.out.println("Swagger UI available at: http://localhost:8080/swagger-ui.html");
        };
    }
}