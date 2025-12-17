package com.example.integration;

import com.example.AbstractIntegrationTest;
import com.example.dto.RestaurantRequestDTO;
import com.example.dto.RestaurantResponseDTO;
import com.example.dto.ReviewRequestDTO;
import com.example.dto.VisitorRequestDTO;
import com.example.entity.CuisineType;
import com.example.service.RestaurantService;
import com.example.service.ReviewService;
import com.example.service.VisitorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FullIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ReviewService reviewService;

    @Test
    void fullScenario_CreateVisitorRestaurantAndReview() {
        // 1. Создание посетителя
        VisitorRequestDTO visitorRequest = new VisitorRequestDTO(
                "Integration Test User", 30, "Man"
        );
        var savedVisitor = visitorService.save(visitorRequest);
        assertNotNull(savedVisitor.id());

        // 2. Создание ресторана
        RestaurantRequestDTO restaurantRequest = new RestaurantRequestDTO(
                "Integration Test Restaurant",
                "Test Description",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );
        var savedRestaurant = restaurantService.save(restaurantRequest);
        assertNotNull(savedRestaurant.id());

        // 3. Создание отзыва
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                savedVisitor.id(),
                savedRestaurant.id(),
                5,
                "Excellent integration test!"
        );
        var savedReview = reviewService.save(reviewRequest);
        assertEquals(5, savedReview.rating());

        // 4. Проверка рейтинга ресторана
        RestaurantResponseDTO updatedRestaurant = restaurantService
                .findById(savedRestaurant.id());
        assertEquals(new BigDecimal("5.00"), updatedRestaurant.rating());

        // 5. Поиск ресторанов с минимальным рейтингом
        List<RestaurantResponseDTO> highRated = restaurantService
                .findRestaurantsWithMinRating(new BigDecimal("4.0"));
        assertTrue(highRated.size() >= 1);

        // 6. Негативный тест - попытка создать дубликат отзыва
        assertThrows(IllegalStateException.class, () -> {
            reviewService.save(reviewRequest);
        });
    }

    @Test
    void negativeTests_HandleExceptions() {
        // Несуществующий ID
        assertThrows(Exception.class, () -> {
            visitorService.findById(999999L);
        });

        // Невалидные данные при создании
        RestaurantRequestDTO invalidRequest = new RestaurantRequestDTO(
                "", // пустое имя
                "", // пустое описание
                null, // null кухня
                new BigDecimal("-100") // отрицательный чек
        );

        // Валидация сработает при вызове контроллера
        // Для сервиса можно добавить @Valid в параметры
    }
}