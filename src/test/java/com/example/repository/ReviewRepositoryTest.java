package com.example.repository;

import com.example.AbstractIntegrationTest;
import com.example.entity.Review;
import com.example.entity.ReviewId;
import com.example.entity.Restaurant;
import com.example.entity.Visitor;
import com.example.entity.CuisineType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Visitor visitor;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        visitorRepository.deleteAll();
        restaurantRepository.deleteAll();

        // Create test visitor
        visitor = new Visitor();
        visitor.setName("Test Visitor");
        visitor.setAge(30);
        visitor.setGender("Man");
        visitor = visitorRepository.save(visitor);

        // Create test restaurant
        restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setDescription("Test Description");
        restaurant.setCuisineType(CuisineType.ITALIAN);
        restaurant.setAverageBill(new BigDecimal("1000.00"));
        restaurant.setRating(new BigDecimal("4.5"));
        restaurant = restaurantRepository.save(restaurant);
    }

    @Test
    void save_ValidReview_ReturnsSaved() {
        // Arrange
        Review review = new Review();
        review.setVisitor(visitor);
        review.setRestaurant(restaurant);
        review.setRating(5);
        review.setReviewText("Excellent!");

        // Act
        Review saved = reviewRepository.save(review);

        // Assert
        assertNotNull(saved);
        assertEquals(5, saved.getRating());
        assertEquals("Excellent!", saved.getReviewText());
        assertEquals(visitor.getId(), saved.getVisitor().getId());
        assertEquals(restaurant.getId(), saved.getRestaurant().getId());
    }

    @Test
    void findByRestaurantId_WithPagination_ReturnsPage() {
        // Arrange
        Review review1 = createReview(5, "Excellent!");
        Review review2 = createReview(4, "Good!");
        Review review3 = createReview(3, "Average");

        reviewRepository.saveAll(List.of(review1, review2, review3));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("rating").descending());

        // Act
        Page<Review> result = reviewRepository.findByRestaurantId(restaurant.getId(), pageable);

        // Assert
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getContent().get(0).getRating()); // Highest first due to sort
    }

    @Test
    void existsByVisitorIdAndRestaurantId_ExistingReview_ReturnsTrue() {
        // Arrange
        Review review = createReview(5, "Test");
        reviewRepository.save(review);

        // Act
        boolean exists = reviewRepository.existsByVisitorIdAndRestaurantId(
                visitor.getId(), restaurant.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByVisitorIdAndRestaurantId_NonExistingReview_ReturnsFalse() {
        // Act
        boolean exists = reviewRepository.existsByVisitorIdAndRestaurantId(999L, 999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByVisitorIdAndRestaurantId_ExistingReview_ReturnsReview() {
        // Arrange
        Review review = createReview(5, "Test");
        reviewRepository.save(review);

        // Act
        Optional<Review> found = reviewRepository.findByVisitorIdAndRestaurantId(
                visitor.getId(), restaurant.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(5, found.get().getRating());
    }

    @Test
    void findReviewsByRestaurantSortedByRatingAsc_ReturnsSortedPage() {
        // Arrange
        Review review1 = createReview(5, "Excellent!");
        Review review2 = createReview(3, "Average");
        Review review3 = createReview(4, "Good!");

        reviewRepository.saveAll(List.of(review1, review2, review3));

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Review> result = reviewRepository.findReviewsByRestaurantSortedByRatingAsc(
                restaurant.getId(), pageable);

        // Assert
        List<Review> content = result.getContent();
        assertEquals(3, result.getTotalElements());
        assertEquals(3, content.get(0).getRating()); // Ascending order
        assertEquals(4, content.get(1).getRating());
        assertEquals(5, content.get(2).getRating());
    }

    private Review createReview(int rating, String reviewText) {
        Review review = new Review();
        review.setVisitor(visitor);
        review.setRestaurant(restaurant);
        review.setRating(rating);
        review.setReviewText(reviewText);
        return review;
    }
}