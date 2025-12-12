package com.example.dto.Review;

public record ReviewResponseDTO(
        Long id,
        Long visitorId,
        Long restaurantId,
        Integer rating,
        String reviewText
) {}