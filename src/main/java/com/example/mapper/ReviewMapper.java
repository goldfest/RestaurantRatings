package com.example.mapper;

import com.example.dto.Review.ReviewRequestDTO;
import com.example.dto.Review.ReviewResponseDTO;
import com.example.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    Review toEntity(ReviewRequestDTO reviewRequestDTO);

    ReviewResponseDTO toResponseDTO(Review review);
}