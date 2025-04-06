package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ReviewServiceInterface {

    ReviewDTO registerReview(ReviewDTO reviewDTO);
    ReviewDTO getReviewById(Long id);
    Page<ReviewDTO> getAllReviews(Pageable pageable);
    Page<ReviewDTO> getReviewByRating(BigDecimal rating, Pageable pageable);
    ReviewDTO modifyReview(Long id, ReviewDTO reviewDTO);
    void removeReview(Long id);
}