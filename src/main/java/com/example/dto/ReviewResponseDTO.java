package com.example.dto;

import java.io.Serializable;

public record ReviewResponseDTO(
        Long visitorId,
        Long restaurantId,
        Integer rating,
        String reviewText
) implements Serializable {}