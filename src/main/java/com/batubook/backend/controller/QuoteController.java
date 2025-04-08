package com.batubook.backend.controller;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.service.serviceInterface.QuoteServiceInterface;
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

@RestController
@RequestMapping("api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteServiceInterface quoteService;
    private static final Logger logger = LoggerFactory.getLogger(QuoteController.class);

    @PostMapping("/create")
    public ResponseEntity<QuoteDTO> createQuote(@Valid @RequestBody QuoteDTO quoteDTO) {
        logger.info("Received request to create a new quote from userId: {}", quoteDTO.getUserId());
        QuoteDTO createdQuote = quoteService.registerQuote(quoteDTO);
        logger.info("Successfully created a new quote with ID: {}", createdQuote.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuote);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteDTO> fetchQuoteById(@PathVariable Long id) {
        logger.info("Received GET request for /api/quotes/{}", id);
        QuoteDTO quoteDTO = quoteService.getQuoteById(id);
        logger.info("Returned response for quote with ID: {}", id);
        return ResponseEntity.ok(quoteDTO);
    }

    @GetMapping
    public ResponseEntity<Page<QuoteDTO>> fetchAllQuotes(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/quotes called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<QuoteDTO> allQuotes = quoteService.getAllQuotes(pageable);
        logger.info("Successfully fetched {} quotes", allQuotes.getNumberOfElements());
        return ResponseEntity.ok(allQuotes);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<QuoteDTO> updateQuote(@PathVariable Long id, @Valid @RequestBody QuoteDTO quoteDTO) {
        logger.info("Received request to update quote with ID: {}", id);
        QuoteDTO updatedQuote = quoteService.modifyQuote(id, quoteDTO);
        logger.info("Successfully updated quote with ID: {}", id);
        return ResponseEntity.ok(updatedQuote);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        logger.info("Received request to delete quote with ID: {}", id);
        quoteService.removeQuote(id);
        logger.info("Successfully deleted quote with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}