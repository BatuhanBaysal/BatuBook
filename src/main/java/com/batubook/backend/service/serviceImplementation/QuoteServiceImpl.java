package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.QuoteEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.QuoteMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.QuoteRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.QuoteServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteServiceInterface {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public QuoteDTO registerQuote(QuoteDTO quoteDTO) {
        logger.info("Creating a new quote");
        try {
            QuoteEntity quoteEntity = mapQuoteDTOToEntity(quoteDTO);
            QuoteEntity savedQuote = quoteRepository.save(quoteEntity);
            logger.info("Quote successfully created with ID: {}", savedQuote.getId());
            return quoteMapper.quoteEntityToQuoteDTO(savedQuote);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating quote: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Quote could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteDTO getQuoteById(Long id) {
        logger.info("Attempting to retrieve quote with ID: {}", id);
        QuoteEntity quoteEntity = quoteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Quote not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("Quote not found with ID: " + id);
                });

        logger.info("Successfully retrieved quote with ID: {}", id);
        return quoteMapper.quoteEntityToQuoteDTO(quoteEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuoteDTO> getAllQuotes(Pageable pageable) {
        logger.debug("Fetching all quotes with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<QuoteEntity> allQuotes = quoteRepository.findAll(pageable);
        logger.info("Successfully fetched {} quotes", allQuotes.getNumberOfElements());
        return allQuotes.map(quoteMapper::quoteEntityToQuoteDTO);
    }

    @Override
    @Transactional
    public QuoteDTO modifyQuote(Long id, QuoteDTO quoteDTO) {
        logger.info("Updating quote with ID: {}", id);
        try {
            QuoteEntity existingQuote = quoteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

            updateQuoteFields(existingQuote, quoteDTO);
            QuoteEntity updatedQuote = quoteRepository.save(existingQuote);
            logger.info("Quote successfully updated with ID: {}", updatedQuote.getId());
            return quoteMapper.quoteEntityToQuoteDTO(updatedQuote);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Quote not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating quote: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Quote could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeQuote(Long id) {
        logger.info("Attempting to remove quote with ID: {}", id);
        if (!quoteRepository.existsById(id)) {
            logger.error("Quote with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Quote not found with ID: " + id);
        }

        quoteRepository.deleteById(id);
        logger.info("Successfully deleted quote with ID: {}", id);
    }

    private QuoteEntity mapQuoteDTOToEntity(QuoteDTO quoteDTO) {
        UserEntity userEntity = userRepository.findById(quoteDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + quoteDTO.getUserId()));

        BookEntity bookEntity = bookRepository.findById(quoteDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + quoteDTO.getBookId()));

        QuoteEntity quoteEntity = quoteMapper.quoteDTOToQuoteEntity(quoteDTO);
        quoteEntity.setUser(userEntity);
        quoteEntity.setBook(bookEntity);
        return quoteEntity;
    }

    private void updateQuoteFields(QuoteEntity existingQuote, QuoteDTO quoteDTO) {
        if (quoteDTO.getQuoteText() != null) {
            existingQuote.setQuoteText(quoteDTO.getQuoteText());
        }

        if (quoteDTO.getUserId() != null && quoteDTO.getBookId() != null) {
            UserEntity userEntity = userRepository.findById(quoteDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + quoteDTO.getUserId()));
            BookEntity bookEntity = bookRepository.findById(quoteDTO.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + quoteDTO.getBookId()));

            existingQuote.setUser(userEntity);
            existingQuote.setBook(bookEntity);
        }
    }
}