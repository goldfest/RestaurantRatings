package com.example.dto;

import jakarta.validation.constraints.*;

public record VisitorRequestDTO(
        @NotBlank(message = "Имя обязательно")
        @Size(min = 1, max = 100, message = "Имя от 1 до 100 символов")
        String name,

        @NotNull(message = "Возраст обязателен")
        @Min(value = 1, message = "Возраст не менее 1")
        @Max(value = 110, message = "Возраст не более 110")
        Integer age,

        @NotBlank(message = "Пол обязателен")
        @Pattern(regexp = "^(Man|Woman|Other)$", message = "Пол должен быть Man, Woman or Other")
        String gender
) {}

