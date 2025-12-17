package com.example.controller;

import com.example.dto.VisitorRequestDTO;
import com.example.dto.VisitorResponseDTO;
import com.example.service.VisitorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitorController.class)
class VisitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VisitorService visitorService;

    @Test
    void createVisitor_ValidRequest_ReturnsCreated() throws Exception {
        VisitorRequestDTO request = new VisitorRequestDTO("John", 25, "Man");
        VisitorResponseDTO response = new VisitorResponseDTO(1L, "John", 25, "Man");

        when(visitorService.save(any(VisitorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.gender").value("Man"));
    }

    @Test
    void createVisitor_InvalidRequest_ReturnsBadRequest() throws Exception {
        VisitorRequestDTO request = new VisitorRequestDTO("", -5, "Invalid");

        mockMvc.perform(post("/api/visitors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllVisitors_ReturnsOk() throws Exception {
        List<VisitorResponseDTO> visitors = List.of(
                new VisitorResponseDTO(1L, "John", 25, "Man"),
                new VisitorResponseDTO(2L, "Anna", 30, "Woman")
        );

        when(visitorService.findAll()).thenReturn(visitors);

        mockMvc.perform(get("/api/visitors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getVisitorById_ExistingId_ReturnsOk() throws Exception {
        VisitorResponseDTO response = new VisitorResponseDTO(1L, "John", 25, "Man");
        when(visitorService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/visitors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getVisitorById_NonExistingId_ReturnsNotFound() throws Exception {
        when(visitorService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/visitors/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateVisitor_ValidRequest_ReturnsOk() throws Exception {
        VisitorRequestDTO request = new VisitorRequestDTO("John Updated", 26, "Man");
        VisitorResponseDTO response = new VisitorResponseDTO(1L, "John Updated", 26, "Man");

        when(visitorService.update(eq(1L), any(VisitorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/visitors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.age").value(26));
    }

    @Test
    void updateVisitor_NonExistingId_ReturnsNotFound() throws Exception {
        VisitorRequestDTO request = new VisitorRequestDTO("John", 25, "Man");
        when(visitorService.update(eq(999L), any(VisitorRequestDTO.class))).thenReturn(null);

        mockMvc.perform(put("/api/visitors/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVisitor_ExistingId_ReturnsNoContent() throws Exception {
        when(visitorService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/visitors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVisitor_NonExistingId_ReturnsNotFound() throws Exception {
        when(visitorService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/visitors/999"))
                .andExpect(status().isNotFound());
    }
}