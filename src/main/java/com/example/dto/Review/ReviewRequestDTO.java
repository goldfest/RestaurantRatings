package com.example.dto.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequestDTO(
        @NotNull(message = "Visitor ID обязательно")
        Long visitorId,

        @NotNull(message = "Restaurant ID обязательно")
        Long restaurantId,

        @NotNull(message = "Оценка обязательна")
        @Min(value = 1, message = "Рейтинг не менее 1")
        @Max(value = 5, message = "Рейтинг не более 5")
        Integer rating,

        @Size(max = 1000, message = "Текст отзыва не более 1000")
        String reviewText
) {}

