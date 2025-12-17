package com.example.service;

import com.example.dto.RestaurantRequestDTO;
import com.example.dto.RestaurantResponseDTO;
import com.example.entity.CuisineType;
import com.example.entity.Restaurant;
import com.example.mapper.RestaurantMapper;
import com.example.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private RestaurantRequestDTO requestDTO;
    private RestaurantResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Italian Paradise");
        restaurant.setDescription("Authentic Italian cuisine");
        restaurant.setCuisineType(CuisineType.ITALIAN);
        restaurant.setAverageBill(new BigDecimal("1500.00"));
        restaurant.setRating(new BigDecimal("4.5"));

        requestDTO = new RestaurantRequestDTO(
                "Italian Paradise",
                "Authentic Italian cuisine",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );

        responseDTO = new RestaurantResponseDTO(
                1L,
                "Italian Paradise",
                "Authentic Italian cuisine",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00"),
                new BigDecimal("4.5")
        );
    }

    @Test
    void save_ValidRequest_ReturnsResponseDTO() {
        when(restaurantMapper.toEntity(requestDTO)).thenReturn(restaurant);
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        when(restaurantMapper.toResponseDTO(restaurant)).thenReturn(responseDTO);

        RestaurantResponseDTO result = restaurantService.save(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(restaurantRepository, times(1)).save(restaurant);
    }

    @Test
    void findById_ExistingId_ReturnsRestaurant() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toResponseDTO(restaurant)).thenReturn(responseDTO);

        RestaurantResponseDTO result = restaurantService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void findById_NonExistingId_ThrowsException() {
        when(restaurantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            restaurantService.findById(999L);
        });
    }

    @Test
    void update_ExistingRestaurant_ReturnsUpdated() {
        RestaurantRequestDTO updateDTO = new RestaurantRequestDTO(
                "Updated Name",
                "Updated Description",
                CuisineType.FRENCH,
                new BigDecimal("2000.00")
        );

        Restaurant updatedRestaurant = new Restaurant();
        updatedRestaurant.setId(1L);
        updatedRestaurant.setName("Updated Name");
        updatedRestaurant.setDescription("Updated Description");
        updatedRestaurant.setCuisineType(CuisineType.FRENCH);
        updatedRestaurant.setAverageBill(new BigDecimal("2000.00"));

        RestaurantResponseDTO updatedResponse = new RestaurantResponseDTO(
                1L,
                "Updated Name",
                "Updated Description",
                CuisineType.FRENCH,
                new BigDecimal("2000.00"),
                new BigDecimal("4.5")
        );

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);
        when(restaurantMapper.toResponseDTO(any(Restaurant.class))).thenReturn(updatedResponse);

        RestaurantResponseDTO result = restaurantService.update(1L, updateDTO);

        assertEquals("Updated Name", result.name());
        assertEquals(CuisineType.FRENCH, result.cuisineType());
    }

    @Test
    void delete_ExistingId_ReturnsTrue() {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        doNothing().when(restaurantRepository).deleteById(1L);

        boolean result = restaurantService.delete(1L);

        assertTrue(result);
        verify(restaurantRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NonExistingId_ThrowsException() {
        when(restaurantRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            restaurantService.delete(999L);
        });
    }

    @Test
    void findAll_ReturnsList() {
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("French Bistro");

        List<Restaurant> restaurants = List.of(restaurant, restaurant2);
        List<RestaurantResponseDTO> responses = List.of(
                responseDTO,
                new RestaurantResponseDTO(2L, "French Bistro", "", CuisineType.FRENCH,
                        new BigDecimal("2000.00"), new BigDecimal("4.7"))
        );

        when(restaurantRepository.findAll()).thenReturn(restaurants);
        when(restaurantMapper.toResponseDTO(restaurant)).thenReturn(responseDTO);
        when(restaurantMapper.toResponseDTO(restaurant2)).thenReturn(responses.get(1));

        List<RestaurantResponseDTO> result = restaurantService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findRestaurantsWithMinRating_ValidRating_ReturnsList() {
        BigDecimal minRating = new BigDecimal("4.0");
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2L);
        restaurant2.setName("French Bistro");
        restaurant2.setRating(new BigDecimal("4.7"));

        RestaurantResponseDTO response2 = new RestaurantResponseDTO(
                2L, "French Bistro", "", CuisineType.FRENCH,
                new BigDecimal("2000.00"), new BigDecimal("4.7")
        );

        when(restaurantRepository.findByRatingGreaterThanEqual(minRating))
                .thenReturn(List.of(restaurant, restaurant2));
        when(restaurantMapper.toResponseDTO(restaurant)).thenReturn(responseDTO);
        when(restaurantMapper.toResponseDTO(restaurant2)).thenReturn(response2);

        List<RestaurantResponseDTO> result = restaurantService
                .findRestaurantsWithMinRating(minRating);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r ->
                r.rating().compareTo(minRating) >= 0));
    }

    @Test
    void findRestaurantsWithMinRatingJpql_ValidRating_ReturnsList() {
        BigDecimal minRating = new BigDecimal("3.5");

        when(restaurantRepository.findRestaurantsWithMinRating(minRating))
                .thenReturn(List.of(restaurant));
        when(restaurantMapper.toResponseDTO(restaurant)).thenReturn(responseDTO);

        List<RestaurantResponseDTO> result = restaurantService
                .findRestaurantsWithMinRatingJpql(minRating);

        assertEquals(1, result.size());
        verify(restaurantRepository, times(1))
                .findRestaurantsWithMinRating(minRating);
    }
}