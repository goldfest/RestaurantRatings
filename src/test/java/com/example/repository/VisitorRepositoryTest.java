package com.example.repository;

import com.example.AbstractIntegrationTest;
import com.example.entity.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VisitorRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private VisitorRepository visitorRepository;

    @BeforeEach
    void setUp() {
        visitorRepository.deleteAll();
    }

    @Test
    void save_ValidVisitor_ReturnsSaved() {
        // Arrange
        Visitor visitor = new Visitor();
        visitor.setName("John Doe");
        visitor.setAge(30);
        visitor.setGender("Man");

        // Act
        Visitor saved = visitorRepository.save(visitor);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getName());
        assertEquals(30, saved.getAge());
        assertEquals("Man", saved.getGender());
    }

    @Test
    void findById_ExistingId_ReturnsVisitor() {
        // Arrange
        Visitor visitor = new Visitor();
        visitor.setName("John Doe");
        visitor.setAge(30);
        visitor.setGender("Man");
        Visitor saved = visitorRepository.save(visitor);

        // Act
        Optional<Visitor> found = visitorRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void findById_NonExistingId_ReturnsEmpty() {
        // Act
        Optional<Visitor> found = visitorRepository.findById(999L);

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_MultipleVisitors_ReturnsAll() {
        // Arrange
        Visitor visitor1 = new Visitor();
        visitor1.setName("John");
        visitor1.setAge(30);
        visitor1.setGender("Man");

        Visitor visitor2 = new Visitor();
        visitor2.setName("Anna");
        visitor2.setAge(25);
        visitor2.setGender("Woman");

        visitorRepository.saveAll(List.of(visitor1, visitor2));

        // Act
        List<Visitor> allVisitors = visitorRepository.findAll();

        // Assert
        assertEquals(2, allVisitors.size());
    }

    @Test
    void deleteById_ExistingId_DeletesVisitor() {
        // Arrange
        Visitor visitor = new Visitor();
        visitor.setName("John Doe");
        visitor.setAge(30);
        visitor.setGender("Man");
        Visitor saved = visitorRepository.save(visitor);

        // Act
        visitorRepository.deleteById(saved.getId());

        // Assert
        Optional<Visitor> found = visitorRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void existsById_ExistingId_ReturnsTrue() {
        // Arrange
        Visitor visitor = new Visitor();
        visitor.setName("John Doe");
        visitor.setAge(30);
        visitor.setGender("Man");
        Visitor saved = visitorRepository.save(visitor);

        // Act
        boolean exists = visitorRepository.existsById(saved.getId());

        // Assert
        assertTrue(exists);
    }
}