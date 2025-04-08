package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.LikeDTO;
import com.batubook.backend.entity.*;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.LikeMapper;
import com.batubook.backend.repository.*;
import com.batubook.backend.service.serviceInterface.LikeServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeServiceInterface {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private static final Logger logger = LoggerFactory.getLogger(LikeServiceImpl.class);

    private final UserRepository userRepository;
    private final BookInteractionRepository bookInteractionRepository;
    private final ReviewRepository reviewRepository;
    private final QuoteRepository quoteRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public LikeDTO registerLike(LikeDTO likeDTO) {
        try {
            if (!isValidLike(likeDTO)) {
                return null;
            }

            LikeEntity likeEntity = new LikeEntity();
            setEntityReference(likeDTO, likeEntity);
            UserEntity user = userRepository.findById(likeDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found for ID: " + likeDTO.getUserId()));

            likeEntity.setUser(user);
            LikeEntity savedLiked = likeRepository.save(likeEntity);
            return likeMapper.likeEntityToDTO(savedLiked);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating like: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Like could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LikeDTO getLikeById(Long id) {
        logger.info("Attempting to retrieve like with ID: {}", id);
        LikeEntity likeEntity = likeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Like not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("Like not found with ID: " + id);
                });

        logger.info("Successfully retrieved like with ID: {}", id);
        return likeMapper.likeEntityToDTO(likeEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LikeDTO> getAllLikes(Pageable pageable) {
        logger.debug("Fetching all likes with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<LikeEntity> allLikes = likeRepository.findAll(pageable);
        logger.info("Successfully fetched {} likes", allLikes.getNumberOfElements());
        return allLikes.map(likeMapper::likeEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getByUserIdAndMessageId(Long userId, Long messageId) {
        logger.info("Checking like existence for UserId: {} and MessageId: {}", userId, messageId);
        boolean exists = likeRepository.existsByUserIdAndMessageId(userId, messageId);
        logger.info("Like existence for UserId: {} and MessageId: {}: {}", userId, messageId, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getByUserIdAndBookInteractionId(Long userId, Long bookInteractionId) {
        logger.info("Checking like existence for UserId: {} and BookInteractionId: {}", userId, bookInteractionId);
        boolean exists = likeRepository.existsByUserIdAndBookInteractionId(userId, bookInteractionId);
        logger.info("Like existence for UserId: {} and BookInteractionId: {}: {}", userId, bookInteractionId, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getByUserIdAndReviewId(Long userId, Long reviewId) {
        logger.info("Checking like existence for UserId: {} and ReviewId: {}", userId, reviewId);
        boolean exists = likeRepository.existsByUserIdAndReviewId(userId, reviewId);
        logger.info("Like existence for UserId: {} and ReviewId: {}: {}", userId, reviewId, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean getByUserIdAndQuoteId(Long userId, Long quoteId) {
        logger.info("Checking like existence for UserId: {} and QuoteId: {}", userId, quoteId);
        boolean exists = likeRepository.existsByUserIdAndQuoteId(userId, quoteId);
        logger.info("Like existence for UserId: {} and QuoteId: {}: {}", userId, quoteId, exists);
        return exists;
    }

    @Override
    @Transactional
    public LikeDTO modifyLike(Long id, LikeDTO likeDTO) {
        try {
            LikeEntity likeEntity = likeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Like not found with ID: " + id));

            updateLikeEntityWithDTO(likeEntity, likeDTO);
            LikeEntity updatedLike = likeRepository.save(likeEntity);
            logger.info("Successfully modified like with ID: {}", id);
            return likeMapper.likeEntityToDTO(updatedLike);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Like not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating like: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Like could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeLike(Long id) {
        logger.info("Attempting to remove like with ID: {}", id);
        if (!likeRepository.existsById(id)) {
            logger.error("Like with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Like not found with ID: " + id);
        }

        likeRepository.deleteById(id);
        logger.info("Successfully deleted like with ID: {}", id);
    }

    private boolean isValidLike(LikeDTO likeDTO) {
        boolean isValid = (likeDTO.getMessageId() != null ||
                likeDTO.getBookInteractionId() != null ||
                likeDTO.getReviewId() != null ||
                likeDTO.getQuoteId() != null);

        if (isValid) {
            int filledCount = 0;
            if (likeDTO.getBookInteractionId() != null) filledCount++;
            if (likeDTO.getReviewId() != null) filledCount++;
            if (likeDTO.getQuoteId() != null) filledCount++;
            if (likeDTO.getMessageId() != null) filledCount++;

            if (filledCount != 1) {
                logger.info("User with ID {} attempted to register a like with multiple references. Only one reference is allowed. BookInteractionId: {}, ReviewId: {}, QuoteId: {}, MessageId: {}",
                        likeDTO.getUserId(), likeDTO.getBookInteractionId(), likeDTO.getReviewId(), likeDTO.getQuoteId(), likeDTO.getMessageId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only one type of like can be selected. Please choose exactly one type of like.");
            }
        }

        return isValid;
    }

    private void setEntityReference(LikeDTO likeDTO, LikeEntity likeEntity) {
        Optional<?> entityOptional = Optional.empty();

        if (likeDTO.getBookInteractionId() != null) {
            entityOptional = findEntityById(likeDTO.getBookInteractionId(), BookInteractionEntity.class);
            entityOptional.ifPresent(entity -> likeEntity.setBookInteraction((BookInteractionEntity) entity));
        } else if (likeDTO.getReviewId() != null) {
            entityOptional = findEntityById(likeDTO.getReviewId(), ReviewEntity.class);
            entityOptional.ifPresent(entity -> likeEntity.setReview((ReviewEntity) entity));
        } else if (likeDTO.getQuoteId() != null) {
            entityOptional = findEntityById(likeDTO.getQuoteId(), QuoteEntity.class);
            entityOptional.ifPresent(entity -> likeEntity.setQuote((QuoteEntity) entity));
        } else if (likeDTO.getMessageId() != null) {
            entityOptional = findEntityById(likeDTO.getMessageId(), MessageEntity.class);
            entityOptional.ifPresent(entity -> likeEntity.setMessage((MessageEntity) entity));
        }

        if (entityOptional.isEmpty()) {
            logger.warn("Entity not found for the provided ID.");
            throw new RuntimeException("Entity not found.");
        }
    }

    private Optional<?> findEntityById(Long id, Class<?> entityClass) {
        if (entityClass == BookInteractionEntity.class) {
            return bookInteractionRepository.findById(id);
        } else if (entityClass == ReviewEntity.class) {
            return reviewRepository.findById(id);
        } else if (entityClass == QuoteEntity.class) {
            return quoteRepository.findById(id);
        } else if (entityClass == MessageEntity.class) {
            return messageRepository.findById(id);
        }
        return Optional.empty();
    }

    private void updateLikeEntityWithDTO(LikeEntity likeEntity, LikeDTO likeDTO) {
        int filledCount = 0;
        likeEntity.setBookInteraction(null);
        likeEntity.setReview(null);
        likeEntity.setQuote(null);
        likeEntity.setMessage(null);

        Optional<?> entityOptional = Optional.empty();

        if (likeDTO.getMessageId() != null) {
            entityOptional = findEntityById(likeDTO.getMessageId(), MessageEntity.class);
            if (entityOptional.isEmpty()) {
                throw new RuntimeException("Message not found with ID: " + likeDTO.getMessageId());
            }
            likeEntity.setMessage((MessageEntity) entityOptional.get());
            filledCount++;
        }
        if (likeDTO.getBookInteractionId() != null) {
            entityOptional = findEntityById(likeDTO.getBookInteractionId(), BookInteractionEntity.class);
            if (entityOptional.isEmpty()) {
                throw new RuntimeException("BookInteraction not found with ID: " + likeDTO.getBookInteractionId());
            }
            likeEntity.setBookInteraction((BookInteractionEntity) entityOptional.get());
            filledCount++;
        }
        if (likeDTO.getReviewId() != null) {
            entityOptional = findEntityById(likeDTO.getReviewId(), ReviewEntity.class);
            if (entityOptional.isEmpty()) {
                throw new RuntimeException("Review not found with ID: " + likeDTO.getReviewId());
            }
            likeEntity.setReview((ReviewEntity) entityOptional.get());
            filledCount++;
        }
        if (likeDTO.getQuoteId() != null) {
            entityOptional = findEntityById(likeDTO.getQuoteId(), QuoteEntity.class);
            if (entityOptional.isEmpty()) {
                throw new RuntimeException("Quote not found with ID: " + likeDTO.getQuoteId());
            }
            likeEntity.setQuote((QuoteEntity) entityOptional.get());
            filledCount++;
        }

        if (filledCount != 1) {
            logger.info("User with ID {} attempted to register a like with multiple references. Only one reference is allowed. BookInteractionId: {}, ReviewId: {}, QuoteId: {}, MessageId: {}",
                    likeDTO.getUserId(), likeDTO.getBookInteractionId(), likeDTO.getReviewId(), likeDTO.getQuoteId(), likeDTO.getMessageId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only one type of like can be selected. Please choose exactly one type of like.");
        }
    }
}