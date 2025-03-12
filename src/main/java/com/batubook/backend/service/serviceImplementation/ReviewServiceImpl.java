package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.ReviewEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.mapper.ReviewMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.ReviewRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.ReviewServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewServiceInterface {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Transactional
    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) {

        logger.info("Creating a new Review. Provided ID (if any): {}", reviewDTO.getId());
        try {
            UserEntity userEntity = userRepository.findById(reviewDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + reviewDTO.getUserId()));

            BookEntity bookEntity = bookRepository.findById(reviewDTO.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + reviewDTO.getBookId()));

            ReviewEntity reviewEntity = reviewMapper.reviewDTOToReviewEntity(reviewDTO);
            reviewEntity.setUser(userEntity);
            reviewEntity.setBook(bookEntity);
            logger.debug("Converted ReviewDTO to ReviewEntity: {}", reviewEntity);

            ReviewEntity savedReview = reviewRepository.save(reviewEntity);
            logger.info("Review saved successfully with ID: {}", savedReview.getId());
            return reviewMapper.reviewEntityToReviewDTO(savedReview);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input provided: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while creating the review: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while creating the review", e);
        }
    }

    @Override
    public ReviewDTO getReviewById(Long id) {

        logger.info("Fetching review with ID: {}", id);
        try {
            ReviewEntity review = reviewRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Review with ID " + id + " not found"));

            return reviewMapper.reviewEntityToReviewDTO(review);
        } catch (EntityNotFoundException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while fetching review with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching review", e);
        }
    }

    @Override
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {

        logger.info("Fetching all reviews with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<ReviewEntity> reviews = reviewRepository.findAll(pageable);
            logger.info("Successfully fetched {} reviews", reviews.getTotalElements());
            return reviews.map(reviewMapper::reviewEntityToReviewDTO);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all reviews: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching all reviews", e);
        }
    }

    @Override
    public Page<ReviewDTO> getReviewByRating(BigDecimal rating, Pageable pageable) {

        try {
            Page<ReviewEntity> reviews = reviewRepository.findByRating(rating, pageable);
            if (reviews.isEmpty()) {
                logger.warn("No reviews found with rating: {}", rating);
            } else {
                logger.info("Successfully fetched reviews with rating: {}", rating);
            }

            return reviews.map(reviewMapper::reviewEntityToReviewDTO);
        } catch (Exception e) {
            logger.error("Error occurred while fetching reviews with rating: {}", rating, e);
            throw new RuntimeException("Error occurred while fetching reviews by rating", e);
        }
    }

    @Transactional
    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {

        logger.info("Updating review with ID: {}", id);
        try {
            ReviewEntity existingReview = reviewRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

            updateReviewDetails(existingReview, reviewDTO);
            ReviewEntity updatedReview = reviewRepository.save(existingReview);
            logger.info("Review updated successfully with ID: {}", id);
            return reviewMapper.reviewEntityToReviewDTO(updatedReview);
        } catch (EntityNotFoundException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while updating review with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while updating review", e);
        }
    }

    private void updateReviewDetails(ReviewEntity reviewEntity, ReviewDTO reviewDTO) {

        if (reviewDTO.getReviewText() != null) {
            reviewEntity.setReviewText(reviewDTO.getReviewText());
        }
        if (reviewDTO.getRating() != null) {
            reviewEntity.setRating(reviewDTO.getRating());
        }
    }

    @Transactional
    @Override
    public void deleteReview(Long id) {

        logger.info("Deleting review with ID: {}", id);
        try {
            ReviewEntity review = reviewRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

            reviewRepository.delete(review);
            logger.info("Review deleted successfully with ID: {}", id);
        } catch (EntityNotFoundException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while deleting review with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while deleting review", e);
        }
    }
}