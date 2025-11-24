package com.example.dto.Restaurant;

import com.example.entity.CuisineType;

import java.math.BigDecimal;

public record RestaurantResponseDTO(
        Long id,
        String name,
        String description,
        CuisineType cuisineType,
        BigDecimal averageBill,
        BigDecimal rating
) {}