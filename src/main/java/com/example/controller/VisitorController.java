package com.example.controller;

import com.example.dto.VisitorRequestDTO;
import com.example.dto.VisitorResponseDTO;
import com.example.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@Tag(name = "Посетители", description = "Управление посетителями")
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping
    @Operation(summary = "Создать посетителя")
    public ResponseEntity<VisitorResponseDTO> createVisitor(@Valid @RequestBody VisitorRequestDTO visitorRequestDTO) {
        VisitorResponseDTO createdVisitor = visitorService.save(visitorRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisitor);
    }

    @GetMapping
    @Operation(summary = "Получить всех посетителей")
    public ResponseEntity<List<VisitorResponseDTO>> getAllVisitors() {
        List<VisitorResponseDTO> visitors = visitorService.findAll();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить посетителя по айди")
    public ResponseEntity<VisitorResponseDTO> getVisitorById(@PathVariable Long id) {
        VisitorResponseDTO visitor = visitorService.findById(id);
        if (visitor != null) {
            return ResponseEntity.ok(visitor);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить посетителя по айди")
    public ResponseEntity<VisitorResponseDTO> updateVisitor(
            @PathVariable Long id,
            @Valid @RequestBody VisitorRequestDTO visitorRequestDTO) {
        VisitorResponseDTO updatedVisitor = visitorService.update(id, visitorRequestDTO);
        if (updatedVisitor != null) {
            return ResponseEntity.ok(updatedVisitor);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить посетителя по айди")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        boolean deleted = visitorService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}