package com.example.service;

import com.example.AbstractIntegrationTest;
import com.example.dto.RestaurantRequestDTO;
import com.example.dto.RestaurantResponseDTO;
import com.example.dto.ReviewRequestDTO;
import com.example.dto.VisitorRequestDTO;
import com.example.entity.CuisineType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ServiceIntegrationWithTestContainersTest extends AbstractIntegrationTest {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ReviewService reviewService;

    private Long visitorId;
    private Long restaurantId;

    @BeforeEach
    void setUp() {
        VisitorRequestDTO visitorRequest = new VisitorRequestDTO("Test Visitor", 30, "Man");
        var savedVisitor = visitorService.save(visitorRequest);
        visitorId = savedVisitor.id();

        RestaurantRequestDTO restaurantRequest = new RestaurantRequestDTO(
                "Test Restaurant",
                "Test Description",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );
        var savedRestaurant = restaurantService.save(restaurantRequest);
        restaurantId = savedRestaurant.id();
    }

    @Test
    void createReviewAndUpdateRating_WorksCorrectly() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                visitorId, restaurantId, 5, "Excellent!"
        );

        reviewService.save(reviewRequest);

        RestaurantResponseDTO restaurant = restaurantService.findById(restaurantId);
        assertEquals(new BigDecimal("5.00"), restaurant.rating());
    }

    @Test
    void duplicateReview_ThrowsException() {
        ReviewRequestDTO reviewRequest = new ReviewRequestDTO(
                visitorId, restaurantId, 5, "First review"
        );
        reviewService.save(reviewRequest);

        assertThrows(IllegalStateException.class, () -> {
            reviewService.save(reviewRequest);
        });
    }

    @Test
    void deleteNonExistingVisitor_ThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> {
            visitorService.delete(999L);
        });
    }

    @Test
    void updateReview_UpdatesRating() {
        ReviewRequestDTO initialReview = new ReviewRequestDTO(
                visitorId, restaurantId, 3, "Average"
        );
        reviewService.save(initialReview);

        ReviewRequestDTO updatedReview = new ReviewRequestDTO(
                visitorId, restaurantId, 5, "Now excellent!"
        );
        reviewService.update(visitorId, restaurantId, updatedReview);

        RestaurantResponseDTO restaurant = restaurantService.findById(restaurantId);
        assertEquals(new BigDecimal("5.00"), restaurant.rating());
    }

    @Test
    void findRestaurantsWithMinRating_IntegrationTest() {
        RestaurantRequestDTO restaurant2 = new RestaurantRequestDTO(
                "Low Rated", "Desc", CuisineType.FRENCH, new BigDecimal("2000")
        );
        var savedRestaurant2 = restaurantService.save(restaurant2);

        VisitorRequestDTO anotherVisitor = new VisitorRequestDTO("Another", 25, "Woman");
        var savedAnotherVisitor = visitorService.save(anotherVisitor);

        reviewService.save(new ReviewRequestDTO(
                savedAnotherVisitor.id(), savedRestaurant2.id(), 2, "Poor"
        ));

        List<RestaurantResponseDTO> highRated = restaurantService
                .findRestaurantsWithMinRating(new BigDecimal("4.0"));

        assertTrue(highRated.stream().allMatch(r ->
                r.rating().compareTo(new BigDecimal("4.0")) >= 0));
    }


}