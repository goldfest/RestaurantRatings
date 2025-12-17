package com.example.controller;

import com.example.dto.RestaurantRequestDTO;
import com.example.dto.RestaurantResponseDTO;
import com.example.entity.CuisineType;
import com.example.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    @Test
    void createRestaurant_ValidRequest_ReturnsCreated() throws Exception {
        RestaurantRequestDTO request = new RestaurantRequestDTO(
                "Italian Paradise",
                "Authentic Italian cuisine",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00")
        );

        RestaurantResponseDTO response = new RestaurantResponseDTO(
                1L,
                "Italian Paradise",
                "Authentic Italian cuisine",
                CuisineType.ITALIAN,
                new BigDecimal("1500.00"),
                new BigDecimal("4.5")
        );

        when(restaurantService.save(any(RestaurantRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Italian Paradise"))
                .andExpect(jsonPath("$.cuisineType").value("ITALIAN"));
    }

    @Test
    void createRestaurant_InvalidRequest_ReturnsBadRequest() throws Exception {
        RestaurantRequestDTO request = new RestaurantRequestDTO(
                "", // пустое имя
                "", // пустое описание
                null, // null кухня
                new BigDecimal("-100") // отрицательный чек
        );

        mockMvc.perform(post("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRestaurants_ReturnsOk() throws Exception {
        List<RestaurantResponseDTO> restaurants = List.of(
                new RestaurantResponseDTO(1L, "Restaurant 1", "Desc 1",
                        CuisineType.ITALIAN, new BigDecimal("1000"), new BigDecimal("4.0")),
                new RestaurantResponseDTO(2L, "Restaurant 2", "Desc 2",
                        CuisineType.FRENCH, new BigDecimal("2000"), new BigDecimal("4.5"))
        );

        when(restaurantService.findAll()).thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getRestaurantById_ExistingId_ReturnsOk() throws Exception {
        RestaurantResponseDTO response = new RestaurantResponseDTO(
                1L, "Test", "Desc",
                CuisineType.ITALIAN, new BigDecimal("1000"), new BigDecimal("4.0")
        );

        when(restaurantService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void getRestaurantById_NonExistingId_ReturnsNotFound() throws Exception {
        when(restaurantService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/restaurants/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRestaurant_ValidRequest_ReturnsOk() throws Exception {
        RestaurantRequestDTO request = new RestaurantRequestDTO(
                "Updated Name",
                "Updated Description",
                CuisineType.FRENCH,
                new BigDecimal("2000.00")
        );

        RestaurantResponseDTO response = new RestaurantResponseDTO(
                1L,
                "Updated Name",
                "Updated Description",
                CuisineType.FRENCH,
                new BigDecimal("2000.00"),
                new BigDecimal("4.5")
        );

        when(restaurantService.update(eq(1L), any(RestaurantRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.cuisineType").value("FRENCH"));
    }

    @Test
    void deleteRestaurant_ExistingId_ReturnsNoContent() throws Exception {
        when(restaurantService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/restaurants/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void findRestaurantsWithMinRating_ValidParam_ReturnsOk() throws Exception {
        List<RestaurantResponseDTO> restaurants = List.of(
                new RestaurantResponseDTO(1L, "Test", "Desc",
                        CuisineType.ITALIAN, new BigDecimal("1000"), new BigDecimal("4.5"))
        );

        when(restaurantService.findRestaurantsWithMinRating(new BigDecimal("4.0")))
                .thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants/search/min-rating")
                        .param("minRating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rating").value(4.5));
    }

    @Test
    void findRestaurantsWithMinRatingJpql_ValidParam_ReturnsOk() throws Exception {
        List<RestaurantResponseDTO> restaurants = List.of(
                new RestaurantResponseDTO(1L, "Test", "Desc",
                        CuisineType.ITALIAN, new BigDecimal("1000"), new BigDecimal("4.2"))
        );

        when(restaurantService.findRestaurantsWithMinRatingJpql(new BigDecimal("4.0")))
                .thenReturn(restaurants);

        mockMvc.perform(get("/api/restaurants/search/min-rating-jpql")
                        .param("minRating", "4.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}