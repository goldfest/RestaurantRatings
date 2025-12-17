package com.example.service;

import com.example.dto.VisitorRequestDTO;
import com.example.dto.VisitorResponseDTO;
import com.example.entity.Visitor;
import com.example.mapper.VisitorMapper;
import com.example.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceTest {

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private VisitorMapper visitorMapper;

    @InjectMocks
    private VisitorService visitorService;

    private Visitor visitor;
    private VisitorRequestDTO requestDTO;
    private VisitorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        visitor = new Visitor();
        visitor.setId(1L);
        visitor.setName("Ivan");
        visitor.setAge(25);
        visitor.setGender("Man");

        requestDTO = new VisitorRequestDTO("Ivan", 25, "Man");
        responseDTO = new VisitorResponseDTO(1L, "Ivan", 25, "Man");
    }

    @Test
    void save_ValidRequest_ReturnsResponseDTO() {
        when(visitorMapper.toEntity(requestDTO)).thenReturn(visitor);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        when(visitorMapper.toResponseDTO(visitor)).thenReturn(responseDTO);

        VisitorResponseDTO result = visitorService.save(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Ivan", result.name());
        verify(visitorRepository, times(1)).save(visitor);
    }

    @Test
    void findById_ExistingId_ReturnsVisitor() {
        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorMapper.toResponseDTO(visitor)).thenReturn(responseDTO);

        VisitorResponseDTO result = visitorService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void findById_NonExistingId_ThrowsException() {
        when(visitorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visitorService.findById(999L);
        });
    }

    @Test
    void update_ExistingVisitor_ReturnsUpdated() {
        VisitorRequestDTO updateDTO = new VisitorRequestDTO("Ivan Updated", 26, "Man");
        Visitor updatedVisitor = new Visitor();
        updatedVisitor.setId(1L);
        updatedVisitor.setName("Ivan Updated");
        updatedVisitor.setAge(26);
        updatedVisitor.setGender("Man");

        VisitorResponseDTO updatedResponse = new VisitorResponseDTO(1L, "Ivan Updated", 26, "Man");

        when(visitorRepository.findById(1L)).thenReturn(Optional.of(visitor));
        when(visitorRepository.save(any(Visitor.class))).thenReturn(updatedVisitor);
        when(visitorMapper.toResponseDTO(any(Visitor.class))).thenReturn(updatedResponse);

        VisitorResponseDTO result = visitorService.update(1L, updateDTO);

        assertEquals("Ivan Updated", result.name());
        assertEquals(26, result.age());
    }

    @Test
    void update_NonExistingVisitor_ThrowsException() {
        VisitorRequestDTO updateDTO = new VisitorRequestDTO("Ivan", 25, "Man");
        when(visitorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            visitorService.update(999L, updateDTO);
        });
    }

    @Test
    void delete_ExistingId_ReturnsTrue() {
        when(visitorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(visitorRepository).deleteById(1L);

        boolean result = visitorService.delete(1L);

        assertTrue(result);
        verify(visitorRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_NonExistingId_ThrowsException() {
        when(visitorRepository.existsById(999L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            visitorService.delete(999L);
        });
    }

    @Test
    void findAll_ReturnsList() {
        Visitor visitor2 = new Visitor();
        visitor2.setId(2L);
        visitor2.setName("Anna");
        visitor2.setAge(30);
        visitor2.setGender("Woman");

        List<Visitor> visitors = List.of(visitor, visitor2);
        List<VisitorResponseDTO> responses = List.of(
                responseDTO,
                new VisitorResponseDTO(2L, "Anna", 30, "Woman")
        );

        when(visitorRepository.findAll()).thenReturn(visitors);
        when(visitorMapper.toResponseDTO(visitor)).thenReturn(responseDTO);
        when(visitorMapper.toResponseDTO(visitor2)).thenReturn(responses.get(1));

        List<VisitorResponseDTO> result = visitorService.findAll();

        assertEquals(2, result.size());
    }
}