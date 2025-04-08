package com.batubook.backend.controller;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.service.serviceImplementation.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceImpl reviewService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @PostMapping("/create")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        logger.info("Received request to create a new review: {}", reviewDTO);
        ReviewDTO createReview = reviewService.registerReview(reviewDTO);
        logger.info("Successfully created review with ID: {}", createReview.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> fetchReviewById(@PathVariable Long id) {
        logger.info("Received GET request for /api/reviews/{}", id);
        ReviewDTO reviewDTO = reviewService.getReviewById(id);
        logger.info("Successfully retrieved review with ID: {}", id);
        return ResponseEntity.ok(reviewDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> fetchAllReviews(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/reviews called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ReviewDTO> allReviews = reviewService.getAllReviews(pageable);
        logger.info("Successfully fetched {} reviews", allReviews.getNumberOfElements());
        return ResponseEntity.ok(allReviews);
    }

    @GetMapping("/reviewRating")
    public ResponseEntity<Page<ReviewDTO>> fetchReviewRating(
            @RequestParam BigDecimal rating,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch reviews with rating: {} and pagination: page {}, size {}",
                rating, pageable.getPageNumber(), pageable.getPageSize());
        Page<ReviewDTO> searchRating = reviewService.getReviewByRating(rating, pageable);
        logger.info("Successfully fetched {} reviews with rating: {} on page {}.",
                searchRating.getTotalElements(), rating, pageable.getPageNumber());
        return ResponseEntity.ok(searchRating);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDTO reviewDTO) {
        logger.info("Received request to update review with ID: {}", id);
        ReviewDTO updatedReview = reviewService.modifyReview(id, reviewDTO);
        logger.info("Successfully updated review with ID: {}", id);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        logger.info("Received request to delete review with ID: {}", id);
        reviewService.removeReview(id);
        logger.info("Successfully deleted review with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}