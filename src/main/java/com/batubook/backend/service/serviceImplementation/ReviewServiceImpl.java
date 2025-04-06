package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.ReviewEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.exception.CustomExceptions;
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
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ReviewDTO registerReview(ReviewDTO reviewDTO) {
        logger.info("Creating a new Review. Provided ID (if any): {}", reviewDTO.getId());
        try {
            UserEntity userEntity = userRepository.findById(reviewDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + reviewDTO.getUserId()));

            BookEntity bookEntity = bookRepository.findById(reviewDTO.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + reviewDTO.getBookId()));

            ReviewEntity reviewEntity = reviewMapper.reviewDTOToEntity(reviewDTO);
            reviewEntity.setUser(userEntity);
            reviewEntity.setBook(bookEntity);
            logger.debug("Converted ReviewDTO to ReviewEntity: {}", reviewEntity);

            ReviewEntity savedReview = reviewRepository.save(reviewEntity);
            logger.info("Review saved successfully with ID: {}", savedReview.getId());
            return reviewMapper.reviewEntityToDTO(savedReview);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating review: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Review could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long id) {
        logger.info("Attempting to retrieve review with ID: {}", id);
        ReviewEntity reviewEntity = reviewRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Review not found with ID: {}", id);
                    return new EntityNotFoundException("Review with ID " + id + " not found");
                });

        logger.info("Successfully retrieved review with ID: {}", id);
        return reviewMapper.reviewEntityToDTO(reviewEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAllReviews(Pageable pageable) {
        logger.debug("Fetching all reviews with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ReviewEntity> allReviews = reviewRepository.findAll(pageable);
        logger.info("Successfully fetched {} reviews", allReviews.getNumberOfElements());
        return allReviews.map(reviewMapper::reviewEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewByRating(BigDecimal rating, Pageable pageable) {
        logger.info("Fetching reviews with rating: {}", rating);
        Page<ReviewEntity> reviews = reviewRepository.findByRating(rating, pageable);
        logger.info("Successfully fetched {} reviews with rating: {}", reviews.getTotalElements(), rating);
        return reviews.map(reviewMapper::reviewEntityToDTO);
    }

    @Override
    @Transactional
    public ReviewDTO modifyReview(Long id, ReviewDTO reviewDTO) {
        logger.info("Updating review with ID: {}", id);
        try {
            ReviewEntity existingReview = reviewRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

            updateReviewDetails(existingReview, reviewDTO);
            ReviewEntity updatedReview = reviewRepository.save(existingReview);
            logger.info("Review updated successfully with ID: {}", id);
            return reviewMapper.reviewEntityToDTO(updatedReview);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Review not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating review: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Review could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeReview(Long id) {
        logger.info("Attempting to remove review with ID: {}", id);
        if (!reviewRepository.existsById(id)) {
            logger.error("Review with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Review not found with ID: " + id);
        }

        reviewRepository.deleteById(id);
        logger.info("Successfully deleted review with ID: {}", id);
    }


    private void updateReviewDetails(ReviewEntity reviewEntity, ReviewDTO reviewDTO) {
        if (reviewDTO.getReviewText() != null) {
            reviewEntity.setReviewText(reviewDTO.getReviewText());
        }
        if (reviewDTO.getRating() != null) {
            reviewEntity.setRating(reviewDTO.getRating());
        }
    }
}