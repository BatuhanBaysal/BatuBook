package com.batubook.backend.controller;

import com.batubook.backend.dto.LikeDTO;
import com.batubook.backend.service.serviceImplementation.LikeServiceImpl;
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
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeServiceImpl likeService;
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @PostMapping("/create")
    public ResponseEntity<LikeDTO> createLike(@Valid @RequestBody LikeDTO likeDTO) {
        logger.info("Received request to create Like for User ID: {}", likeDTO.getUserId());
        LikeDTO createdLike = likeService.registerLike(likeDTO);
        logger.info("Like created successfully for User ID: {}. Like ID: {}", likeDTO.getUserId(), createdLike.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LikeDTO> fetchLikeById(@PathVariable Long id) {
        logger.info("Received GET request for /api/likes/{}", id);
        LikeDTO likeDTO = likeService.getLikeById(id);
        logger.info("Returned response for like with ID: {}", id);
        return ResponseEntity.ok(likeDTO);
    }

    @GetMapping
    public ResponseEntity<Page<LikeDTO>> fetchAllLikes(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/likes called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<LikeDTO> allLikes = likeService.getAllLikes(pageable);
        logger.info("Successfully fetched {} likes", allLikes.getNumberOfElements());
        return ResponseEntity.ok(allLikes);
    }

    @GetMapping("/checkLike/message")
    public ResponseEntity<Boolean> checkLikeByUserIdAndMessageId(@RequestParam Long userId, @RequestParam Long messageId) {
        logger.info("Received request to check like existence for UserId: {} and MessageId: {}", userId, messageId);
        boolean exists = likeService.getByUserIdAndMessageId(userId, messageId);
        logger.info("Checked like existence for UserId: {} and MessageId: {}. Like exists: {}", userId, messageId, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkLike/book-interaction")
    public ResponseEntity<Boolean> checkLikeByUserIdAndBookInteractionId(@RequestParam Long userId, @RequestParam Long bookInteractionId) {
        logger.info("Received request to check like existence for UserId: {} and BookInteractionId: {}", userId, bookInteractionId);
        boolean exists = likeService.getByUserIdAndBookInteractionId(userId, bookInteractionId);
        logger.info("Checked like existence for UserId: {} and BookInteractionId: {}. Like exists: {}", userId, bookInteractionId, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkLike/review")
    public ResponseEntity<Boolean> checkLikeByUserIdAndReviewId(@RequestParam Long userId, @RequestParam Long reviewId) {
        logger.info("Received request to check like existence for UserId: {} and ReviewId: {}", userId, reviewId);
        boolean exists = likeService.getByUserIdAndReviewId(userId, reviewId);
        logger.info("Checked like existence for UserId: {} and ReviewId: {}. Like exists: {}", userId, reviewId, exists);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/checkLike/quote")
    public ResponseEntity<Boolean> checkLikeByUserIdAndQuoteId(@RequestParam Long userId, @RequestParam Long quoteId) {
        logger.info("Received request to check like existence for UserId: {} and QuoteId: {}", userId, quoteId);
        boolean exists = likeService.getByUserIdAndQuoteId(userId, quoteId);
        logger.info("Checked like existence for UserId: {} and QuoteId: {}. Like exists: {}", userId, quoteId, exists);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LikeDTO> updateLike(@PathVariable Long id, @Valid @RequestBody LikeDTO likeDTO) {
        logger.info("Attempting to update like with ID: {}. User ID: {}", id, likeDTO.getUserId());
        LikeDTO updatedLike = likeService.modifyLike(id, likeDTO);
        logger.info("Successfully updated like with ID: {} for User ID: {}", id, likeDTO.getUserId());
        return ResponseEntity.ok(updatedLike);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        logger.info("Received request to delete like with ID: {}", id);
        likeService.removeLike(id);
        logger.info("Successfully removed like with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}