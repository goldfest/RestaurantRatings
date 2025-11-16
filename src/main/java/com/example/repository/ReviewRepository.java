package com.example.repository;

import com.example.entity.Review;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReviewRepository {
    private final List<Review> reviews = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Review save(Review review) {
        if (review.getId() == null) {
            review.setId(idCounter.getAndIncrement());
            reviews.add(review);
            return review;
        } else {
            Optional<Review> existingReview = findById(review.getId());
            if (existingReview.isPresent()) {
                reviews.remove(existingReview.get());
                reviews.add(review);
                return review;
            }
            return null;
        }
    }

    public boolean remove(Long id) {
        return reviews.removeIf(review -> review.getId().equals(id));
    }

    public List<Review> findAll() {
        return new ArrayList<>(reviews);
    }

    public Optional<Review> findById(Long id) {
        return reviews.stream()
                .filter(review -> review.getId().equals(id))
                .findFirst();
    }

    public List<Review> findByRestaurantId(Long restaurantId) {
        return reviews.stream()
                .filter(review -> review.getRestaurantId().equals(restaurantId))
                .toList();
    }
}