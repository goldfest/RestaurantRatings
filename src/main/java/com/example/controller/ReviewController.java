package com.example.controller;

import com.example.dto.ReviewRequestDTO;
import com.example.dto.ReviewResponseDTO;
import com.example.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Управление отзывами")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Создать отзыв")
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO createdReview = reviewService.save(reviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping
    @Operation(summary = "Получить все отзывы")
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        List<ReviewResponseDTO> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/visitor/{visitorId}/restaurant/{restaurantId}")
    @Operation(summary = "Получить отзыв по айди")
    public ResponseEntity<ReviewResponseDTO> getReview(
            @PathVariable Long visitorId,
            @PathVariable Long restaurantId) {
        ReviewResponseDTO review = reviewService.findById(visitorId, restaurantId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/visitor/{visitorId}/restaurant/{restaurantId}")
    @Operation(summary = "Обновить отзыв по айди")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long visitorId,
            @PathVariable Long restaurantId,
            @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO updatedReview = reviewService.update(visitorId, restaurantId, reviewRequestDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/visitor/{visitorId}/restaurant/{restaurantId}")
    @Operation(summary = "Удалить отзыв по айди")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long visitorId,
            @PathVariable Long restaurantId) {
        reviewService.delete(visitorId, restaurantId);
        return ResponseEntity.noContent().build();
    }

    // Пагинация с сортировкой (Требование 2)
    @GetMapping("/restaurant/{restaurantId}/page")
    @Operation(summary = "Получить отзыв по айди ресторана с пагинацией")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByRestaurantWithPagination(
            @PathVariable Long restaurantId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field", example = "rating")
            @RequestParam(defaultValue = "rating") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {

        Page<ReviewResponseDTO> reviews = reviewService.getReviewsByRestaurant(
                restaurantId, page, size, sortBy, direction);
        return ResponseEntity.ok(reviews);
    }

    // Пагинация с сортировкой по рейтингу
    @GetMapping("/restaurant/{restaurantId}/sorted")
    @Operation(summary = "Получить отзывы с сортировкой по оценке")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsSortedByRating(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean ascending) {

        Page<ReviewResponseDTO> reviews = reviewService.getReviewsByRestaurantSortedByRating(
                restaurantId, page, size, ascending);
        return ResponseEntity.ok(reviews);
    }
}