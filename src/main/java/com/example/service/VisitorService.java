package com.example.service;

import com.example.dto.Visitor.VisitorRequestDTO;
import com.example.dto.Visitor.VisitorResponseDTO;
import com.example.entity.Visitor;
import com.example.mapper.VisitorMapper;
import com.example.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorService {
    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;

    public VisitorResponseDTO save(VisitorRequestDTO visitorRequestDTO) {
        Visitor visitor = visitorMapper.toEntity(visitorRequestDTO);
        Visitor savedVisitor = visitorRepository.save(visitor);
        return visitorMapper.toResponseDTO(savedVisitor);
    }

    public VisitorResponseDTO update(Long id, VisitorRequestDTO visitorRequestDTO) {
        Visitor visitor = visitorMapper.toEntity(visitorRequestDTO);
        visitor.setId(id);
        Visitor updatedVisitor = visitorRepository.save(visitor);
        return visitorMapper.toResponseDTO(updatedVisitor);
    }

    public boolean remove(Long id) {
        return visitorRepository.remove(id);
    }

    public List<VisitorResponseDTO> findAll() {
        return visitorRepository.findAll().stream()
                .map(visitorMapper::toResponseDTO)
                .toList();
    }

    public VisitorResponseDTO findById(Long id) {
        return visitorRepository.findById(id)
                .map(visitorMapper::toResponseDTO)
                .orElse(null);
    }
}