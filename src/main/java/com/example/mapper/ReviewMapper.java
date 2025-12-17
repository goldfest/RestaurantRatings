package com.example.mapper;

import com.example.dto.ReviewRequestDTO;
import com.example.dto.ReviewResponseDTO;
import com.example.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    Review toEntity(ReviewRequestDTO reviewRequestDTO);

    @Mapping(target = "visitorId", source = "visitor.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    ReviewResponseDTO toResponseDTO(Review review);
}