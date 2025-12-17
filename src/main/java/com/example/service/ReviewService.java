package com.example.service;

import com.example.dto.ReviewRequestDTO;
import com.example.dto.ReviewResponseDTO;
import com.example.entity.Review;
import com.example.entity.ReviewId;
import com.example.entity.Restaurant;
import com.example.entity.Visitor;
import com.example.mapper.ReviewMapper;
import com.example.repository.RestaurantRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final VisitorRepository visitorRepository;
    private final ReviewMapper reviewMapper;

    public ReviewResponseDTO save(ReviewRequestDTO reviewRequestDTO) {
        // Проверяем существование посетителя и ресторана
        Visitor visitor = visitorRepository.findById(reviewRequestDTO.visitorId())
                .orElseThrow(() -> new EntityNotFoundException("Посетитель не найден с id: " + reviewRequestDTO.visitorId()));

        Restaurant restaurant = restaurantRepository.findById(reviewRequestDTO.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Ресторан не найден с id: " + reviewRequestDTO.restaurantId()));

        // Проверяем, не оставлял ли уже пользователь отзыв для этого ресторана
        if (reviewRepository.existsByVisitorIdAndRestaurantId(
                reviewRequestDTO.visitorId(), reviewRequestDTO.restaurantId())) {
            throw new IllegalStateException("Посетитель уже оставил отзыв");
        }

        Review review = reviewMapper.toEntity(reviewRequestDTO);
        review.setVisitor(visitor);
        review.setRestaurant(restaurant);

        Review savedReview = reviewRepository.save(review);
        updateRestaurantRating(restaurant.getId());

        return reviewMapper.toResponseDTO(savedReview);
    }

    public ReviewResponseDTO update(Long visitorId, Long restaurantId, ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewRepository.findByVisitorIdAndRestaurantId(visitorId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Отзыв не найден у visitorId: " + visitorId + " и у restaurantId: " + restaurantId));

        review.setRating(reviewRequestDTO.rating());
        review.setReviewText(reviewRequestDTO.reviewText());

        Review updatedReview = reviewRepository.save(review);
        updateRestaurantRating(restaurantId);

        return reviewMapper.toResponseDTO(updatedReview);
    }

    public void delete(Long visitorId, Long restaurantId) {
        ReviewId reviewId = new ReviewId(visitorId, restaurantId);
        if (!reviewRepository.existsById(reviewId)) {
            throw new EntityNotFoundException(
                    "Отзыв не найден у visitorId: " + visitorId + " и у restaurantId: " + restaurantId);
        }

        reviewRepository.deleteById(reviewId);
        updateRestaurantRating(restaurantId);
    }

    public List<ReviewResponseDTO> findAll() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toResponseDTO)
                .toList();
    }

    public ReviewResponseDTO findById(Long visitorId, Long restaurantId) {
        Review review = reviewRepository.findByVisitorIdAndRestaurantId(visitorId, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Отзыв не найден у visitorId: " + visitorId + " и у restaurantId: " + restaurantId));
        return reviewMapper.toResponseDTO(review);
    }

    // Метод для получения отзывов с пагинацией и сортировкой (Требование 2)
    public Page<ReviewResponseDTO> getReviewsByRestaurant(Long restaurantId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviews = reviewRepository.findByRestaurantId(restaurantId, pageable);

        return reviews.map(reviewMapper::toResponseDTO);
    }

    // Метод с кастомной сортировкой по рейтингу
    public Page<ReviewResponseDTO> getReviewsByRestaurantSortedByRating(
            Long restaurantId, int page, int size, boolean ascending) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = ascending
                ? reviewRepository.findReviewsByRestaurantSortedByRatingAsc(restaurantId, pageable)
                : reviewRepository.findReviewsByRestaurantSortedByRatingDesc(restaurantId, pageable);

        return reviews.map(reviewMapper::toResponseDTO);
    }

    private void updateRestaurantRating(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Ресторан не найден с id: " + restaurantId));

        List<Review> reviews = restaurant.getReviews();

        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
                    .setScale(2, RoundingMode.HALF_UP);

            restaurant.setRating(roundedRating);
            restaurantRepository.save(restaurant);
        } else {
            restaurant.setRating(BigDecimal.ZERO);
            restaurantRepository.save(restaurant);
        }
    }
}