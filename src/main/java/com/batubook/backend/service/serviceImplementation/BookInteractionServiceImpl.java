package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.BookInteractionDTO;
import com.batubook.backend.entity.BookInteractionEntity;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.BookInteractionMapper;
import com.batubook.backend.repository.BookInteractionRepository;
import com.batubook.backend.service.serviceInterface.BookInteractionServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookInteractionServiceImpl implements BookInteractionServiceInterface {

    private final BookInteractionRepository bookInteractionRepository;
    private final BookInteractionMapper bookInteractionMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookInteractionServiceImpl.class);

    @Override
    @Transactional
    public BookInteractionDTO registerBookInteraction(BookInteractionDTO bookInteractionDTO) {
        try {
            logger.info("Book interaction registration started for user: {}", bookInteractionDTO.getUserId());
            if (!bookInteractionDTO.getIsRead()) {
                String message = bookInteractionDTO.getIsLiked()
                        ? "A book must be read before it can be liked."
                        : "You cannot create an interaction with a book that has not been read.";
                logger.error("{} userId: {}, bookId: {}", message, bookInteractionDTO.getUserId(), bookInteractionDTO.getBookId());
                throw new IllegalArgumentException(message);
            }

            BookInteractionEntity entity = bookInteractionMapper.bookInteractionDTOToEntity(bookInteractionDTO);
            logger.info("Saving book interaction for userId: {} and bookId: {}", bookInteractionDTO.getUserId(), bookInteractionDTO.getBookId());
            BookInteractionEntity savedEntity = bookInteractionRepository.save(entity);
            logger.info("Book interaction successfully registered for user: {}", bookInteractionDTO.getUserId());
            return bookInteractionMapper.bookInteractionEntityToDTO(savedEntity);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating book interaction: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Book Interaction could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookInteractionDTO getBookInteractionById(Long id) {
        logger.info("Attempting to retrieve book interaction with ID: {}", id);
        BookInteractionEntity bookInteractionEntity = bookInteractionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Book interaction not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("Book interaction not found with ID: " + id);
                });

        logger.info("Successfully retrieved book interaction with ID: {}", id);
        return bookInteractionMapper.bookInteractionEntityToDTO(bookInteractionEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookInteractionDTO> getAllBookInteractions(Pageable pageable) {
        logger.debug("Fetching all book interactions with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookInteractionEntity> allBookInteractions = bookInteractionRepository.findAll(pageable);
        logger.info("Successfully fetched {} book interactions", allBookInteractions.getNumberOfElements());
        return allBookInteractions.map(bookInteractionMapper::bookInteractionEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookInteractionDTO> getByUserIdAndIsReadTrue(Long userId, Pageable pageable) {
        logger.info("Fetching read books for user with ID: {}. Pagination - Page Number: {}, Page Size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return bookInteractionRepository.findByUserIdAndIsReadTrue(userId, pageable)
                .map(bookInteractionMapper::bookInteractionEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookInteractionDTO> getByUserIdAndIsLikedTrue(Long userId, Pageable pageable) {
        logger.info("Fetching liked books for user with ID: {}. Pagination - Page Number: {}, Page Size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return bookInteractionRepository.findByUserIdAndIsLikedTrue(userId, pageable)
                .map(bookInteractionMapper::bookInteractionEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookInteractionDTO> getByBookIdAndIsReadTrue(Long bookId, Pageable pageable) {
        logger.info("Fetching users who read the book with ID: {}. Pagination - Page Number: {}, Page Size: {}",
                bookId, pageable.getPageNumber(), pageable.getPageSize());
        return bookInteractionRepository.findByBookIdAndIsReadTrue(bookId, pageable)
                .map(bookInteractionMapper::bookInteractionEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookInteractionDTO> getByBookIdAndIsLikedTrue(Long bookId, Pageable pageable) {
        logger.info("Fetching users who liked the book with ID: {}. Pagination - Page Number: {}, Page Size: {}",
                bookId, pageable.getPageNumber(), pageable.getPageSize());
        return bookInteractionRepository.findByBookIdAndIsLikedTrue(bookId, pageable)
                .map(bookInteractionMapper::bookInteractionEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookReadByUser(Long userId, Long bookId) {
        logger.info("Checking read status for UserId: {} and BookId: {}", userId, bookId);
        boolean exists = bookInteractionRepository.existsByUserIdAndBookIdAndIsReadTrue(userId, bookId);
        logger.info("Read status for UserId: {} and BookId: {}: {}", userId, bookId, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookLikedByUser(Long userId, Long bookId) {
        logger.info("Checking like status for UserId: {} and BookId: {}", userId, bookId);
        boolean exists = bookInteractionRepository.existsByUserIdAndBookIdAndIsLikedTrue(userId, bookId);
        logger.info("Like status for UserId: {} and BookId: {}: {}", userId, bookId, exists);
        return exists;
    }

    @Override
    @Transactional
    public BookInteractionDTO modifyBookInteraction(Long id, BookInteractionDTO bookInteractionDTO) {
        try {
            logger.info("Attempting to modify book interaction with id: {}", id);
            BookInteractionEntity existingEntity = bookInteractionRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Book interaction with id: {} not found.", id);
                        return new RuntimeException("Book interaction not found.");
                    });

            if (!bookInteractionDTO.getIsRead()) {
                if (bookInteractionDTO.getIsLiked()) {
                    logger.error("Invalid state: Book must be read before it can be liked. userId: {}, bookId: {}",
                            bookInteractionDTO.getUserId(), bookInteractionDTO.getBookId());
                    throw new IllegalArgumentException("A book must be read before it can be liked.");
                }

                logger.info("User has marked the book as not read, deleting previous record.");
                bookInteractionRepository.delete(existingEntity);
            } else {
                updateInteractionFields(existingEntity, bookInteractionDTO);
            }

            BookInteractionEntity updatedEntity = bookInteractionRepository.save(existingEntity);
            logger.info("Successfully updated book interaction with id: {}", id);
            return bookInteractionMapper.bookInteractionEntityToDTO(updatedEntity);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Book Interaction not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating book interaction: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Book Interaction could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeBookInteraction(Long id) {
        logger.info("Attempting to remove book interaction with ID: {}", id);
        if (!bookInteractionRepository.existsById(id)) {
            logger.error("Book interaction with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Book interaction not found with ID: " + id);
        }

        bookInteractionRepository.deleteById(id);
        logger.info("Successfully deleted book interaction with ID: {}", id);
    }

    private void updateInteractionFields(BookInteractionEntity existingEntity, BookInteractionDTO bookInteractionDTO) {
        logger.info("Existing Book Interaction before modification: Description={}, Read={}, Liked={}",
                existingEntity.getDescription(), existingEntity.getIsRead(), existingEntity.getIsLiked());

        existingEntity.setDescription(bookInteractionDTO.getDescription());
        existingEntity.setIsRead(bookInteractionDTO.getIsRead());
        existingEntity.setIsLiked(bookInteractionDTO.getIsLiked());

        logger.info("Modified Book Interaction: Description={}, Read={}, Liked={}",
                bookInteractionDTO.getDescription(), bookInteractionDTO.getIsRead(), bookInteractionDTO.getIsLiked());
    }
}