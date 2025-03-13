package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.QuoteEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.mapper.QuoteMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.QuoteRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.QuoteServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteServiceInterface {

    private final QuoteRepository quoteRepository;
    private final QuoteMapper quoteMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);

    @Override
    public QuoteDTO createQuote(QuoteDTO quoteDTO) {

        logger.info("Creating a new quote");
        try {
            UserEntity userEntity = userRepository.findById(quoteDTO.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + quoteDTO.getUserId()));
            BookEntity bookEntity = bookRepository.findById(quoteDTO.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + quoteDTO.getBookId()));

            QuoteEntity quoteEntity = quoteMapper.quoteDTOToQuoteEntity(quoteDTO);
            quoteEntity.setUser(userEntity);
            quoteEntity.setBook(bookEntity);

            QuoteEntity savedQuote = quoteRepository.save(quoteEntity);
            logger.info("Quote successfully created with ID: {}", savedQuote.getId());
            return quoteMapper.quoteEntityToQuoteDTO(savedQuote);
        } catch (Exception e) {
            logger.error("Error occurred while creating quote", e);
            throw new RuntimeException("Error creating quote");
        }
    }

    @Override
    public QuoteDTO getQuoteById(Long id) {

        logger.info("Fetching quote with ID: {}", id);
        return quoteRepository.findById(id)
                .map(quoteMapper::quoteEntityToQuoteDTO)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));
    }

    @Override
    public Page<QuoteDTO> getAllQuotes(Pageable pageable) {

        logger.info("Fetching all quotes");
        Page<QuoteEntity> quotes = quoteRepository.findAll(pageable);
        return new PageImpl<>(quotes.map(quoteMapper::quoteEntityToQuoteDTO).getContent(), pageable, quotes.getTotalElements());
    }

    @Override
    public QuoteDTO updateQuote(Long id, QuoteDTO quoteDTO) {

        logger.info("Updating quote with ID: {}", id);
        try {
            QuoteEntity existingQuote = quoteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

            existingQuote.setQuoteText(quoteDTO.getQuoteText());
            QuoteEntity updatedQuote = quoteRepository.save(existingQuote);
            logger.info("Quote successfully updated with ID: {}", updatedQuote.getId());
            return quoteMapper.quoteEntityToQuoteDTO(updatedQuote);
        } catch (Exception e) {
            logger.error("Error occurred while updating quote", e);
            throw new RuntimeException("Error updating quote");
        }
    }

    @Override
    public void deleteQuote(Long id) {

        logger.info("Deleting quote with ID: {}", id);
        try {
            quoteRepository.deleteById(id);
            logger.info("Quote successfully deleted");
        } catch (Exception e) {
            logger.error("Error occurred while deleting quote", e);
            throw new RuntimeException("Error deleting quote");
        }
    }
}