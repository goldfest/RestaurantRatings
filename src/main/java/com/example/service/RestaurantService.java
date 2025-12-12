package com.example.service;

import com.example.dto.RestaurantRequestDTO;
import com.example.dto.RestaurantResponseDTO;
import com.example.entity.Restaurant;
import com.example.mapper.RestaurantMapper;
import com.example.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantResponseDTO save(RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant restaurant = restaurantMapper.toEntity(restaurantRequestDTO);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDTO(savedRestaurant);
    }

    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO restaurantRequestDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + id));

        restaurant.setName(restaurantRequestDTO.name());
        restaurant.setDescription(restaurantRequestDTO.description());
        restaurant.setCuisineType(restaurantRequestDTO.cuisineType());
        restaurant.setAverageBill(restaurantRequestDTO.averageBill());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toResponseDTO(updatedRestaurant);
    }

    public boolean delete(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new EntityNotFoundException("Ресторан не найден с id: " + id);
        }
        restaurantRepository.deleteById(id);
        return true;
    }

    public List<RestaurantResponseDTO> findAll() {
        return restaurantRepository.findAll().stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();
    }

    public RestaurantResponseDTO findById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ресторан не найден с id: " + id));
        return restaurantMapper.toResponseDTO(restaurant);
    }

    // Метод для поиска ресторанов с минимальным рейтингом (Требование 3)
    public List<RestaurantResponseDTO> findRestaurantsWithMinRating(BigDecimal minRating) {
        return restaurantRepository.findByRatingGreaterThanEqual(minRating).stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();
    }

    // Альтернативный метод с использованием JPQL
    public List<RestaurantResponseDTO> findRestaurantsWithMinRatingJpql(BigDecimal minRating) {
        return restaurantRepository.findRestaurantsWithMinRating(minRating).stream()
                .map(restaurantMapper::toResponseDTO)
                .toList();
    }
}