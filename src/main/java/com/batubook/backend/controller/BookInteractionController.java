package com.batubook.backend.controller;

import com.batubook.backend.dto.BookInteractionDTO;
import com.batubook.backend.service.serviceImplementation.BookInteractionServiceImpl;
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
@RequestMapping("/api/book-interactions")
@RequiredArgsConstructor
public class BookInteractionController {

    private final BookInteractionServiceImpl bookInteractionService;
    private static final Logger logger = LoggerFactory.getLogger(BookInteractionController.class);

    @PostMapping("/create")
    public ResponseEntity<BookInteractionDTO> createInteraction(@Valid @RequestBody BookInteractionDTO bookInteractionDTO) {
        logger.info("Creating new book interaction for userId: {} and bookId: {}", bookInteractionDTO.getUserId(), bookInteractionDTO.getBookId());
        BookInteractionDTO createdInteraction = bookInteractionService.registerBookInteraction(bookInteractionDTO);
        logger.info("Created new book interaction with id: {}", createdInteraction.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInteraction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookInteractionDTO> fetchBookInteractionById(@PathVariable Long id) {
        logger.info("Received GET request for /api/book-interactions/{}", id);
        BookInteractionDTO bookInteractionDTO = bookInteractionService.getBookInteractionById(id);
        logger.info("Returned response for book interaction with ID: {}", id);
        return ResponseEntity.ok(bookInteractionDTO);
    }

    @GetMapping
    public ResponseEntity<Page<BookInteractionDTO>> fetchAllBookInteractions(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/book-interactions called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookInteractionDTO> allBookInteractions = bookInteractionService.getAllBookInteractions(pageable);
        logger.info("Successfully fetched {} book interactions", allBookInteractions.getNumberOfElements());
        return ResponseEntity.ok(allBookInteractions);
    }

    @GetMapping("/readInteractionsByUser")
    public ResponseEntity<Page<BookInteractionDTO>> fetchReadInteractionsByUser(
            @RequestParam Long userId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Fetching read interactions for user with ID: {} with pagination (Page: {}, Size: {})",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookInteractionDTO> interactions = bookInteractionService.getByUserIdAndIsReadTrue(userId, pageable);
        logger.info("Fetched {} read interactions for user with ID: {}", interactions.getTotalElements(), userId);
        return ResponseEntity.ok(interactions);
    }

    @GetMapping("/likedInteractionsByUser")
    public ResponseEntity<Page<BookInteractionDTO>> fetchLikedInteractionsByUser(
            @RequestParam Long userId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Fetching liked interactions for user with ID: {} with pagination (Page: {}, Size: {})",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookInteractionDTO> interactions = bookInteractionService.getByUserIdAndIsLikedTrue(userId, pageable);
        logger.info("Fetched {} liked interactions for user with ID: {}", interactions.getTotalElements(), userId);
        return ResponseEntity.ok(interactions);
    }

    @GetMapping("/readInteractionsByBook")
    public ResponseEntity<Page<BookInteractionDTO>> fetchReadInteractionsByBook(
            @RequestParam Long bookId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Fetching read interactions for book with ID: {} with pagination (Page: {}, Size: {})",
                bookId, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookInteractionDTO> interactions = bookInteractionService.getByBookIdAndIsReadTrue(bookId, pageable);
        logger.info("Fetched {} read interactions for book with ID: {}", interactions.getTotalElements(), bookId);
        return ResponseEntity.ok(interactions);
    }

    @GetMapping("/likedInteractionsByBook")
    public ResponseEntity<Page<BookInteractionDTO>> fetchLikedInteractionsByBook(
            @RequestParam Long bookId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Fetching liked interactions for book with ID: {} with pagination (Page: {}, Size: {})",
                bookId, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookInteractionDTO> interactions = bookInteractionService.getByBookIdAndIsLikedTrue(bookId, pageable);
        logger.info("Fetched {} liked interactions for book with ID: {}", interactions.getTotalElements(), bookId);
        return ResponseEntity.ok(interactions);
    }

    @GetMapping("/isBookReadByUser")
    public ResponseEntity<Boolean> isBookReadByUser(@RequestParam Long userId, @RequestParam Long bookId) {
        logger.info("Checking if user with ID: {} has read the book with ID: {}", userId, bookId);
        boolean isRead = bookInteractionService.isBookReadByUser(userId, bookId);
        logger.info("User with ID: {} has read the book with ID: {}: {}", userId, bookId, isRead);
        return ResponseEntity.ok(isRead);
    }

    @GetMapping("/isBookLikedByUser")
    public ResponseEntity<Boolean> isBookLikedByUser(@RequestParam Long userId, @RequestParam Long bookId) {
        logger.info("Checking if user with ID: {} has liked the book with ID: {}", userId, bookId);
        boolean isLiked = bookInteractionService.isBookLikedByUser(userId, bookId);
        logger.info("User with ID: {} has liked the book with ID: {}: {}", userId, bookId, isLiked);
        return ResponseEntity.ok(isLiked);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookInteractionDTO> updateInteraction(@PathVariable Long id, @Valid @RequestBody BookInteractionDTO bookInteractionDTO) {
        logger.info("Updating book interaction with id: {}", id);
        BookInteractionDTO updatedInteraction = bookInteractionService.modifyBookInteraction(id, bookInteractionDTO);
        logger.info("Updated book interaction with id: {}", id);
        return ResponseEntity.ok(updatedInteraction);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id) {
        logger.info("Received request to delete book interaction with ID: {}", id);
        bookInteractionService.removeBookInteraction(id);
        logger.info("Deleted book interaction with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}