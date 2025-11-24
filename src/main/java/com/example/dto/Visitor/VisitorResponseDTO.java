package com.example.dto.Visitor;

public record VisitorResponseDTO(
        Long id,
        String name,
        Integer age,
        String gender
) {}