package com.example.mapper;

import com.example.dto.RestaurantRequestDTO;
import com.example.dto.RestaurantResponseDTO;
import com.example.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    Restaurant toEntity(RestaurantRequestDTO restaurantRequestDTO);

    RestaurantResponseDTO toResponseDTO(Restaurant restaurant);
}