package com.example.service;

import com.example.dto.Review.ReviewRequestDTO;
import com.example.dto.Review.ReviewResponseDTO;
import com.example.entity.Review;
import com.example.mapper.ReviewMapper;
import com.example.repository.RestaurantRepository;
import com.example.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewMapper reviewMapper;

    public ReviewResponseDTO save(ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewMapper.toEntity(reviewRequestDTO);
        Review savedReview = reviewRepository.save(review);
        if (savedReview != null) {
            updateRestaurantRating(review.getRestaurantId());
        }
        return reviewMapper.toResponseDTO(savedReview);
    }

    public ReviewResponseDTO update(Long id, ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewMapper.toEntity(reviewRequestDTO);
        review.setId(id);
        Review updatedReview = reviewRepository.save(review);
        if (updatedReview != null) {
            updateRestaurantRating(review.getRestaurantId());
        }
        return reviewMapper.toResponseDTO(updatedReview);
    }

    public boolean remove(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            Long restaurantId = review.get().getRestaurantId();
            boolean removed = reviewRepository.remove(id);
            if (removed) {
                updateRestaurantRating(restaurantId);
            }
            return removed;
        }
        return false;
    }

    public List<ReviewResponseDTO> findAll() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toResponseDTO)
                .toList();
    }

    public ReviewResponseDTO findById(Long id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toResponseDTO)
                .orElse(null);
    }

    public List<ReviewResponseDTO> findByRestaurantId(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId).stream()
                .map(reviewMapper::toResponseDTO)
                .toList();
    }

    private void updateRestaurantRating(Long restaurantId) {
        List<Review> restaurantReviews = reviewRepository.findByRestaurantId(restaurantId);

        if (!restaurantReviews.isEmpty()) {
            double averageRating = restaurantReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
                    .setScale(2, RoundingMode.HALF_UP);

            restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
                restaurant.setRating(roundedRating);
                restaurantRepository.save(restaurant);
            });
        }
    }
}