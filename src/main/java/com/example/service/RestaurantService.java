package com.example.service;

import com.example.dto.Restaurant.RestaurantRequestDTO;
import com.example.dto.Restaurant.RestaurantResponseDTO;
import com.example.entity.Restaurant;
import com.example.mapper.RestaurantMapper;
import com.example.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantResponseDTO save(RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant restaurant = restaurantMapper.toEntity(restaurantRequestDTO);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDTO(savedRestaurant);
    }

    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant restaurant = restaurantMapper.toEntity(restaurantRequestDTO);
        restaurant.setId(id);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDTO(updatedRestaurant);
    }

    public boolean remove(Long id) {
        return restaurantRepository.remove(id);
    }

    public List<RestaurantResponseDTO> findAll() {
        return restaurantRepository.findAll().stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();
    }

    public RestaurantResponseDTO findById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantMapper::toResponseDTO)
                .orElse(null);
    }
}