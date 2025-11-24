package com.example.mapper;

import com.example.dto.Visitor.VisitorRequestDTO;
import com.example.dto.Visitor.VisitorResponseDTO;
import com.example.entity.Visitor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitorMapper {

    @Mapping(target = "id", ignore = true)
    Visitor toEntity(VisitorRequestDTO visitorRequestDTO);

    VisitorResponseDTO toResponseDTO(Visitor visitor);
}