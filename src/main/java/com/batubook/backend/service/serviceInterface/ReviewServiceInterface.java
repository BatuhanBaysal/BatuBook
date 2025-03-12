package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ReviewServiceInterface {

    ReviewDTO createReview(ReviewDTO reviewDTO);
    ReviewDTO getReviewById(Long id);
    Page<ReviewDTO> getAllReviews(Pageable pageable);
    Page<ReviewDTO> getReviewByRating(BigDecimal rating, Pageable pageable);
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);
    void deleteReview(Long id);
}