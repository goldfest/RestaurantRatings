package com.example.repository;

import com.example.entity.Visitor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VisitorRepository {
    private final List<Visitor> visitors = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Visitor save(Visitor visitor) {
        if (visitor.getId() == null) {
            visitor.setId(idCounter.getAndIncrement());
            visitors.add(visitor);
            return visitor;
        } else {
            Optional<Visitor> existingVisitor = findById(visitor.getId());
            if (existingVisitor.isPresent()) {
                visitors.remove(existingVisitor.get());
                visitors.add(visitor);
                return visitor;
            }
            return null;
        }
    }

    public boolean remove(Long id) {
        return visitors.removeIf(visitor -> visitor.getId().equals(id));
    }

    public List<Visitor> findAll() {
        return new ArrayList<>(visitors);
    }

    public Optional<Visitor> findById(Long id) {
        return visitors.stream()
                .filter(visitor -> visitor.getId().equals(id))
                .findFirst();
    }
}