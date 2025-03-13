package com.batubook.backend.controller;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.service.serviceInterface.QuoteServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/quotes")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QuoteController {

    private final QuoteServiceInterface quoteService;
    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    @PostMapping
    public ResponseEntity<QuoteDTO> createQuote(@Valid @RequestBody QuoteDTO quoteDTO) {

        logger.info("Received request to create a new quote");
        try {
            return ResponseEntity.ok(quoteService.createQuote(quoteDTO));
        } catch (Exception e) {
            logger.error("Error creating quote", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDTO> getQuoteById(@PathVariable Long id) {

        logger.info("Received request to fetch quote with ID: {}", id);
        try {
            return ResponseEntity.ok(quoteService.getQuoteById(id));
        } catch (Exception e) {
            logger.error("Error fetching quote", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<QuoteDTO>> getAllQuotes(Pageable pageable) {

        logger.info("Received request to fetch all quotes");
        return ResponseEntity.ok(quoteService.getAllQuotes(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteDTO> updateQuote(@PathVariable Long id, @Valid @RequestBody QuoteDTO quoteDTO) {

        logger.info("Received request to update quote with ID: {}", id);
        try {
            return ResponseEntity.ok(quoteService.updateQuote(id, quoteDTO));
        } catch (Exception e) {
            logger.error("Error updating quote", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {

        logger.info("Received request to delete quote with ID: {}", id);
        try {
            quoteService.deleteQuote(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting quote", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}