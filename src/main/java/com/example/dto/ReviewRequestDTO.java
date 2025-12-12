package com.example.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ReviewRequestDTO(
        @NotNull(message = "Visitor ID обязателен")
        Long visitorId,

        @NotNull(message = "Restaurant ID обязателен")
        Long restaurantId,

        @NotNull(message = "Рейтинг обязателен")
        @Min(value = 1, message = "Не менее 1")
        @Max(value = 5, message = "Не более 5")
        Integer rating,

        @Size(max = 1000, message = "Текст не более 1000 символов")
        String reviewText
) implements Serializable {}

