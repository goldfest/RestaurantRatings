package com.example.service;

import com.example.dto.ReviewRequestDTO;
import com.example.dto.ReviewResponseDTO;
import com.example.entity.*;
import com.example.mapper.ReviewMapper;
import com.example.repository.RestaurantRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private Visitor visitor;
    private Restaurant restaurant;
    private Review review;
    private ReviewRequestDTO requestDTO;
    private ReviewResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        visitor = new Visitor();
        visitor.setId(1L);
        visitor.setName("Ivan");

        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Italian Paradise");
        restaurant.setRating(new BigDecimal("4.5"));

        review = new Review();
        review.setVisitor(visitor);
        review.setRestaurant(restaurant);
        review.setRating(5);
        review.setReviewText("беллисимо");

        requestDTO = new ReviewRequestDTO(1L, 1L, 5, "беллисимо");
        responseDTO = new ReviewResponseDTO(1L, 1L, 5, "беллисимо");
    }

    @Test
    void save_ValidRequest_ReturnsResponseDTO() {
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reviewRepository.existsByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(false);
        when(reviewMapper.toEntity(requestDTO)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

        doNothing().when(restaurantRepository).save(any(Restaurant.class));

        ReviewResponseDTO result = reviewService.save(requestDTO);

        assertNotNull(result);
        assertEquals(5, result.rating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void save_DuplicateReview_ThrowsException() {
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reviewRepository.existsByVisitorIdAndRestaurantId(1L, 1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            reviewService.save(requestDTO);
        });
    }

    @Test
    void save_NonExistingVisitor_ThrowsException() {
        when(visitorRepository.findById(999L)).thenReturn(Optional.empty());

        ReviewRequestDTO invalidRequest = new ReviewRequestDTO(999L, 1L, 5, "Test");

        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.save(invalidRequest);
        });
    }

    @Test
    void findById_ExistingReview_ReturnsReview() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L))
                .thenReturn(Optional.of(review));
        when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

        ReviewResponseDTO result = reviewService.findById(1L, 1L);

        assertNotNull(result);
        assertEquals(5, result.rating());
    }

    @Test
    void findById_NonExistingReview_ThrowsException() {
        when(reviewRepository.findByVisitorIdAndRestaurantId(999L, 999L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.findById(999L, 999L);
        });
    }

    @Test
    void update_ExistingReview_ReturnsUpdated() {
        ReviewRequestDTO updateDTO = new ReviewRequestDTO(1L, 1L, 4, "Updated text");
        Review updatedReview = new Review();
        updatedReview.setVisitor(visitor);
        updatedReview.setRestaurant(restaurant);
        updatedReview.setRating(4);
        updatedReview.setReviewText("Updated text");

        ReviewResponseDTO updatedResponse = new ReviewResponseDTO(1L, 1L, 4, "Updated text");

        when(reviewRepository.findByVisitorIdAndRestaurantId(1L, 1L))
                .thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);
        when(reviewMapper.toResponseDTO(any(Review.class))).thenReturn(updatedResponse);
        doNothing().when(restaurantRepository).save(any(Restaurant.class));

        ReviewResponseDTO result = reviewService.update(1L, 1L, updateDTO);

        assertEquals(4, result.rating());
        assertEquals("Updated text", result.reviewText());
    }

    @Test
    void delete_ExistingReview_DeletesSuccessfully() {
        ReviewId reviewId = new ReviewId(1L, 1L);
        when(reviewRepository.existsById(reviewId)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(reviewId);
        doNothing().when(restaurantRepository).save(any(Restaurant.class));

        assertDoesNotThrow(() -> {
            reviewService.delete(1L, 1L);
        });

        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    @Test
    void delete_NonExistingReview_ThrowsException() {
        when(reviewRepository.existsById(new ReviewId(999L, 999L))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.delete(999L, 999L);
        });
    }

    @Test
    void findAll_ReturnsList() {
        List<Review> reviews = List.of(review);
        List<ReviewResponseDTO> responses = List.of(responseDTO);

        when(reviewRepository.findAll()).thenReturn(reviews);
        when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

        List<ReviewResponseDTO> result = reviewService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void getReviewsByRestaurant_ValidParameters_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review));

        when(reviewRepository.findByRestaurantId(1L, pageable))
                .thenReturn(reviewPage);
        when(reviewMapper.toResponseDTO(review)).thenReturn(responseDTO);

        Page<ReviewResponseDTO> result = reviewService
                .getReviewsByRestaurant(1L, 0, 10, "rating", "asc");

        assertEquals(1, result.getTotalElements());
    }
}