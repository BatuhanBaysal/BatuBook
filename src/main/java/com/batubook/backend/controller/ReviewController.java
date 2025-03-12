package com.batubook.backend.controller;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.service.serviceImplementation.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReviewController {

    private final ReviewServiceImpl reviewService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @PostMapping("/createReview")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {

        logger.info("Received request to create a new review: {}", reviewDTO);
        try {
            ReviewDTO createReview = reviewService.createReview(reviewDTO);
            logger.info("Successfully created review with ID: {}", createReview.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createReview);
        } catch (Exception e) {
            logger.error("Error occurred while creating review: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/reviewId/{id}")
    public ResponseEntity<ReviewDTO> searchReviewById(@PathVariable Long id) {

        logger.info("Received request to fetch review with ID: {}", id);
        try {
            ReviewDTO reviewById = reviewService.getReviewById(id);
            logger.info("Successfully fetched review with ID: {}", id);
            return ResponseEntity.ok(reviewById);
        } catch (Exception e) {
            logger.error("Error occurred while fetching review with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/allReviews")
    public ResponseEntity<Page<ReviewDTO>> searchAllReviews(Pageable pageable) {

        logger.info("Received request to fetch all reviews with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<ReviewDTO> allReviews = reviewService.getAllReviews(pageable);
            logger.info("Successfully fetched {} reviews", allReviews.getTotalElements());
            return ResponseEntity.ok(allReviews);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all reviews: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/reviewRating")
    public ResponseEntity<Page<ReviewDTO>> searchReviewRating(@RequestParam BigDecimal rating, Pageable pageable) {

        logger.info("Received request to fetch reviews with rating: {}", rating);
        try {
            Page<ReviewDTO> searchRating = reviewService.getReviewByRating(rating, pageable);
            if (searchRating.isEmpty()) {
                logger.warn("No reviews found with rating: {}", rating);
            } else {
                logger.info("Successfully fetched reviews with rating: {}", rating);
            }

            return ResponseEntity.ok(searchRating);
        } catch (Exception e) {
            logger.error("Error occurred while fetching reviews by rating: {}", rating, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updateReview/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDTO reviewDTO) {

        logger.info("Received request to update review with ID: {}", id);
        try {
            ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
            logger.info("Successfully updated review with ID: {}", id);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            logger.error("Error occurred while updating review with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {

        logger.info("Received request to delete review with ID: {}", id);
        try {
            reviewService.deleteReview(id);
            logger.info("Successfully deleted review with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Review deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting review with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting review");
        }
    }
}