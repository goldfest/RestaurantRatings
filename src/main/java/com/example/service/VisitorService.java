package com.example.service;

import com.example.dto.VisitorRequestDTO;
import com.example.dto.VisitorResponseDTO;
import com.example.entity.Visitor;
import com.example.mapper.VisitorMapper;
import com.example.repository.VisitorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorService {
    private final VisitorRepository visitorRepository;
    private final VisitorMapper visitorMapper;

    public VisitorResponseDTO save(VisitorRequestDTO visitorRequestDTO) {
        Visitor visitor = visitorMapper.toEntity(visitorRequestDTO);
        Visitor savedVisitor = visitorRepository.save(visitor);
        return visitorMapper.toResponseDTO(savedVisitor);
    }

    public VisitorResponseDTO update(Long id, VisitorRequestDTO visitorRequestDTO) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Посетитель не найден с id: " + id));

        visitor.setName(visitorRequestDTO.name());
        visitor.setAge(visitorRequestDTO.age());
        visitor.setGender(visitorRequestDTO.gender());

        Visitor updatedVisitor = visitorRepository.save(visitor);
        return visitorMapper.toResponseDTO(updatedVisitor);
    }

    public boolean delete(Long id) {
        if (!visitorRepository.existsById(id)) {
            throw new EntityNotFoundException("Посетитель не найден с id: " + id);
        }
        visitorRepository.deleteById(id);
        return true;
    }

    public List<VisitorResponseDTO> findAll() {
        return visitorRepository.findAll().stream()
                .map(visitorMapper::toResponseDTO)
                .toList();
    }

    public VisitorResponseDTO findById(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Посетитель не найден с id: " + id));
        return visitorMapper.toResponseDTO(visitor);
    }
}