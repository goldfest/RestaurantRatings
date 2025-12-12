package com.example.dto.Restaurant;

import com.example.entity.CuisineType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RestaurantRequestDTO(
        @NotBlank(message = "Имя обязательно")
        @Size(min = 1, max = 200, message = "Название от 1 до 200 символов")
        String name,

        @NotBlank(message = "Описание обязательно")
        @Size(min = 1, max = 1000, message = "Описание от 1 до 1000 символов")
        String description,

        @NotNull(message = "Вид кухни обязателен")
        CuisineType cuisineType,

        @NotNull(message = "Средний чек обязателен")
        @DecimalMin(value = "0.0", inclusive = false, message = "Средний чек больше 0")
        @Digits(integer = 10, fraction = 2, message = "Средний чек должен быть в допустимом десятичном формате")
        BigDecimal averageBill
) {}

