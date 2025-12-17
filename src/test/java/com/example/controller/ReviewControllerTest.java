package com.example.controller;

import com.example.dto.ReviewRequestDTO;
import com.example.dto.ReviewResponseDTO;
import com.example.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    void createReview_ValidRequest_ReturnsCreated() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(1L, 1L, 5, "Excellent!");
        ReviewResponseDTO response = new ReviewResponseDTO(1L, 1L, 5, "Excellent!");

        when(reviewService.save(any(ReviewRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visitorId").value(1L))
                .andExpect(jsonPath("$.restaurantId").value(1L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewText").value("Excellent!"));
    }

    @Test
    void createReview_InvalidRequest_ReturnsBadRequest() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(null, null, 6, "");

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllReviews_ReturnsOk() throws Exception {
        List<ReviewResponseDTO> reviews = List.of(
                new ReviewResponseDTO(1L, 1L, 5, "Excellent!"),
                new ReviewResponseDTO(2L, 1L, 4, "Good!")
        );

        when(reviewService.findAll()).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getReview_ExistingReview_ReturnsOk() throws Exception {
        ReviewResponseDTO response = new ReviewResponseDTO(1L, 1L, 5, "Great!");
        when(reviewService.findById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/api/reviews/visitor/1/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.reviewText").value("Great!"));
    }

    @Test
    void updateReview_ValidRequest_ReturnsOk() throws Exception {
        ReviewRequestDTO request = new ReviewRequestDTO(1L, 1L, 4, "Updated review");
        ReviewResponseDTO response = new ReviewResponseDTO(1L, 1L, 4, "Updated review");

        when(reviewService.update(eq(1L), eq(1L), any(ReviewRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/reviews/visitor/1/restaurant/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.reviewText").value("Updated review"));
    }

    @Test
    void deleteReview_ExistingReview_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/reviews/visitor/1/restaurant/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getReviewsByRestaurantWithPagination_ValidParams_ReturnsPage() throws Exception {
        ReviewResponseDTO review = new ReviewResponseDTO(1L, 1L, 5, "Great!");
        Page<ReviewResponseDTO> page = new PageImpl<>(List.of(review));

        when(reviewService.getReviewsByRestaurant(anyLong(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/reviews/restaurant/1/page")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "rating")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].rating").value(5));
    }

    @Test
    void getReviewsSortedByRating_ValidParams_ReturnsPage() throws Exception {
        ReviewResponseDTO review1 = new ReviewResponseDTO(1L, 1L, 5, "Excellent!");
        ReviewResponseDTO review2 = new ReviewResponseDTO(2L, 1L, 4, "Good!");
        Page<ReviewResponseDTO> page = new PageImpl<>(List.of(review1, review2));

        when(reviewService.getReviewsByRestaurantSortedByRating(anyLong(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(page);

        mockMvc.perform(get("/api/reviews/restaurant/1/sorted")
                        .param("page", "0")
                        .param("size", "10")
                        .param("ascending", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }
}