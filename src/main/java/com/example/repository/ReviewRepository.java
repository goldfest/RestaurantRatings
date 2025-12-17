package com.example.repository;

import com.example.entity.Review;
import com.example.entity.ReviewId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

    //найти все отзывы по ресторану с пагинацией
    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    //найти все отзывы посетителя
    List<Review> findByVisitorId(Long visitorId);

    //проверить, существует ли отзыв от пользователя для ресторана
    boolean existsByVisitorIdAndRestaurantId(Long visitorId, Long restaurantId);

    //найти отзыв по посетителю и ресторану
    Optional<Review> findByVisitorIdAndRestaurantId(Long visitorId, Long restaurantId);

    //получение отзывов с сортировкой
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId ORDER BY r.rating ASC")
    Page<Review> findReviewsByRestaurantSortedByRatingAsc(
            @Param("restaurantId") Long restaurantId,
            Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId ORDER BY r.rating DESC")
    Page<Review> findReviewsByRestaurantSortedByRatingDesc(
            @Param("restaurantId") Long restaurantId,
            Pageable pageable);
}