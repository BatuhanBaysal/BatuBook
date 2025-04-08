package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.RepostSaveDTO;
import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.ActionType;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.RepostSaveMapper;
import com.batubook.backend.repository.*;
import com.batubook.backend.service.serviceInterface.RepostSaveServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepostSaveServiceImpl implements RepostSaveServiceInterface {

    private final RepostSaveRepository repostSaveRepository;
    private final RepostSaveMapper repostSaveMapper;
    private static final Logger logger = LoggerFactory.getLogger(RepostSaveServiceImpl.class);

    private final UserRepository userRepository;
    private final BookInteractionRepository bookInteractionRepository;
    private final ReviewRepository reviewRepository;
    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public RepostSaveDTO registerRepostSave(RepostSaveDTO repostSaveDTO) {
        try {
            logger.info("Registering repost/save action for userId: {}", repostSaveDTO.getUserId());
            validateRepostSave(repostSaveDTO);

            RepostSaveEntity repostSaveEntity = new RepostSaveEntity();
            setContentToRepostSaveEntity(repostSaveEntity, repostSaveDTO);

            RepostSaveEntity savedRepost = repostSaveRepository.save(repostSaveEntity);
            logger.info("Repost/save action successfully registered for userId: {}", repostSaveDTO.getUserId());
            return repostSaveMapper.repostSaveEntityToDTO(savedRepost);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating Repost-save: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Repost-save could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RepostSaveDTO getRepostSaveById(Long id) {
        logger.info("Attempting to retrieve repost/save with ID: {}", id);
        RepostSaveEntity repostSaveEntity = repostSaveRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("RepostSaveEntity not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("RepostSaveEntity not found with ID: " + id);
                });

        logger.info("Successfully retrieved repost/save with ID: {}", id);
        return repostSaveMapper.repostSaveEntityToDTO(repostSaveEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RepostSaveDTO> getAllRepostSaves(Pageable pageable) {
        logger.debug("Fetching all report saves with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<RepostSaveEntity> allRepostSaves = repostSaveRepository.findAll(pageable);
        logger.info("Successfully fetched {} repost/save actions", allRepostSaves.getNumberOfElements());
        return allRepostSaves.map(repostSaveMapper::repostSaveEntityToDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RepostSaveDTO> getByUserId(Long userId, Pageable pageable) {
        logger.info("Started fetching repost/save records for userId: {} with pagination: page {} size {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<RepostSaveEntity> repostSaveEntities = repostSaveRepository.findByUserId(userId, pageable);
        logger.info("Successfully fetched {} repost/save records for userId: {}. Total pages: {}, Total elements: {}",
                repostSaveEntities.getTotalElements(), userId, repostSaveEntities.getTotalPages(), repostSaveEntities.getTotalElements());
        return repostSaveEntities.map(repostSaveMapper::repostSaveEntityToDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<RepostSaveDTO> getByUserIdAndActionType(Long userId, ActionType actionType, Pageable pageable) {
        logger.info("Started fetching repost/save records for userId: {} with actionType: {} and pagination: page {} size {}",
                userId, actionType, pageable.getPageNumber(), pageable.getPageSize());
        Page<RepostSaveEntity> repostSaveEntities = repostSaveRepository.findByUserIdAndActionType(userId, actionType, pageable);
        logger.info("Successfully fetched {} repost/save records for userId: {} with actionType: {}. Total pages: {}, Total elements: {}",
                repostSaveEntities.getTotalElements(), userId, actionType, repostSaveEntities.getTotalPages(), repostSaveEntities.getTotalElements());
        return repostSaveEntities.map(repostSaveMapper::repostSaveEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RepostSaveDTO getByUserIdAndContent(Long userId, Long reviewId, Long quoteId, Long bookInteractionId) {
        logger.info("Searching for repost/save for userId: {}, reviewId: {}, quoteId: {}, bookInteractionId: {}",
                userId, reviewId, quoteId, bookInteractionId);
        RepostSaveEntity repostSaveEntity = repostSaveRepository.findByUserIdAndContent(userId, reviewId, quoteId, bookInteractionId)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("Repost/save not found for userId: " + userId +
                        " with reviewId: " + reviewId + ", quoteId: " + quoteId +
                        ", bookInteractionId: " + bookInteractionId));
        logger.info("Successfully found repost/save for userId: {}, reviewId: {}, quoteId: {}, bookInteractionId: {}",
                userId, reviewId, quoteId, bookInteractionId);
        return repostSaveMapper.repostSaveEntityToDTO(repostSaveEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByUserIdAndContentAndActionType(Long userId, Long reviewId, Long quoteId, Long bookInteractionId, ActionType actionType) {
        logger.info("Checking if repost/save exists for userId: {}, reviewId: {}, quoteId: {}, bookInteractionId: {}, actionType: {}",
                userId, reviewId, quoteId, bookInteractionId, actionType);
        boolean exists = repostSaveRepository.existsByUserIdAndContentAndActionType(userId, reviewId, quoteId, bookInteractionId, actionType);
        logger.info("Repost/save exists for userId: {}, reviewId: {}, quoteId: {}, bookInteractionId: {}, actionType: {}: {}",
                userId, reviewId, quoteId, bookInteractionId, actionType, exists);
        return exists;
    }

    @Override
    @Transactional
    public RepostSaveDTO modifyRepostSave(Long id, RepostSaveDTO repostSaveDTO) {
        try {
            logger.info("Modifying repost/save action for userId: {} with repostSaveId: {}", repostSaveDTO.getUserId(), id);
            RepostSaveEntity repostSaveEntity = repostSaveRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("RepostSaveEntity not found for modify with id: {}", id);
                        return new CustomExceptions.NotFoundException("RepostSaveEntity not found with id: " + id);
                    });

            validateRepostSave(repostSaveDTO);
            setContentToRepostSaveEntity(repostSaveEntity, repostSaveDTO);
            RepostSaveEntity updatedRepostSave = repostSaveRepository.save(repostSaveEntity);
            logger.info("Repost/save action successfully modified for userId: {} with repostSaveId: {}", repostSaveDTO.getUserId(), id);
            return repostSaveMapper.repostSaveEntityToDTO(updatedRepostSave);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Repost-save not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating Repost-save: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Repost-save could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeRepostSave(Long id) {
        logger.info("Attempting to remove repost-save with ID: {}", id);
        if (!repostSaveRepository.existsById(id)) {
            logger.error("Repost-save with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Repost-save not found with ID: " + id);
        }

        repostSaveRepository.deleteById(id);
        logger.info("Successfully deleted repost-save with ID: {}", id);
    }

    private void validateRepostSave(RepostSaveDTO repostSaveDTO) {
        if (repostSaveDTO.getActionType() == null) {
            logger.error("Action type is not specified for userId: {}", repostSaveDTO.getUserId());
            throw new IllegalArgumentException("Action type (REPOST/SAVE) must be specified.");
        }

        validateContentTypes(repostSaveDTO.getReviewId(), repostSaveDTO.getQuoteId(), repostSaveDTO.getBookInteractionId());
    }

    private void validateContentTypes(Long reviewId, Long quoteId, Long bookInteractionId) {
        int nonNullCount = 0;
        if (reviewId != null) nonNullCount++;
        if (quoteId != null) nonNullCount++;
        if (bookInteractionId != null) nonNullCount++;

        logger.info("Non-null content types count: {}", nonNullCount);
        if (nonNullCount > 1) {
            logger.error("Multiple content types specified. reviewId: {}, quoteId: {}, bookInteractionId: {}",
                    reviewId, quoteId, bookInteractionId);
            throw new IllegalStateException("Only one content type (Review, Quote, or BookInteraction) can be referenced.");
        }
    }

    private void setContentToRepostSaveEntity(RepostSaveEntity repostSaveEntity, RepostSaveDTO repostSaveDTO) {
        if (repostSaveDTO.getActionType() != null) {
            repostSaveEntity.setActionType(repostSaveDTO.getActionType());
        }

        if (repostSaveDTO.getUserId() != null) {
            UserEntity user = userRepository.findById(repostSaveDTO.getUserId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("User not found with id: " + repostSaveDTO.getUserId()));
            repostSaveEntity.setUser(user);
        }

        if (repostSaveDTO.getReviewId() != null) {
            ReviewEntity review = reviewRepository.findById(repostSaveDTO.getReviewId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Review not found with id: " + repostSaveDTO.getReviewId()));
            repostSaveEntity.setReview(review);
            repostSaveEntity.setQuote(null);
            repostSaveEntity.setBookInteraction(null);
        } else if (repostSaveDTO.getQuoteId() != null) {
            QuoteEntity quote = quoteRepository.findById(repostSaveDTO.getQuoteId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Quote not found with id: " + repostSaveDTO.getQuoteId()));
            repostSaveEntity.setQuote(quote);
            repostSaveEntity.setReview(null);
            repostSaveEntity.setBookInteraction(null);
        } else if (repostSaveDTO.getBookInteractionId() != null) {
            BookInteractionEntity bookInteraction = bookInteractionRepository.findById(repostSaveDTO.getBookInteractionId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("BookInteraction not found with id: " + repostSaveDTO.getBookInteractionId()));
            repostSaveEntity.setBookInteraction(bookInteraction);
            repostSaveEntity.setReview(null);
            repostSaveEntity.setQuote(null);
        }
    }
}