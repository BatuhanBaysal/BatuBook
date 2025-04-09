package com.batubook.backend.controller;

import com.batubook.backend.dto.RepostSaveDTO;
import com.batubook.backend.entity.enums.ActionType;
import com.batubook.backend.service.serviceImplementation.RepostSaveServiceImpl;
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
@RequestMapping("/api/repost-saves")
@RequiredArgsConstructor
public class RepostSaveController {

    private final RepostSaveServiceImpl repostSaveService;
    private static final Logger logger = LoggerFactory.getLogger(RepostSaveController.class);

    @PostMapping("/create")
    public ResponseEntity<RepostSaveDTO> createRepostSave(@Valid @RequestBody RepostSaveDTO repostSaveDTO) {
        logger.info("Received request to create repost/save action for userId: {}", repostSaveDTO.getUserId());
        RepostSaveDTO repostSave = repostSaveService.registerRepostSave(repostSaveDTO);
        logger.info("Repost/save action successfully registered for userId: {}", repostSaveDTO.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(repostSave);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepostSaveDTO> fetchRepostSaveById(@PathVariable Long id) {
        logger.info("Received GET request for /api/repost-saves/{}", id);
        RepostSaveDTO repostSaveDTO = repostSaveService.getRepostSaveById(id);
        logger.info("Successfully retrieved repost/save with ID: {}", id);
        return ResponseEntity.ok(repostSaveDTO);
    }

    @GetMapping
    public ResponseEntity<Page<RepostSaveDTO>> fetchAllRepostSaves(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/repost-saves called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<RepostSaveDTO> allRepostSaves = repostSaveService.getAllRepostSaves(pageable);
        logger.info("Successfully fetched {} repost/save actions", allRepostSaves.getNumberOfElements());
        return ResponseEntity.ok(allRepostSaves);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<RepostSaveDTO>> fetchRepostSavesByUserId(
            @RequestParam Long userId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Started fetching repost/save actions for userId: {} with pagination: page {} size {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<RepostSaveDTO> repostSavesByUser = repostSaveService.getByUserId(userId, pageable);
        logger.info("Successfully fetched {} repost/save actions for userId: {}. Total pages: {}, Total elements: {}",
                repostSavesByUser.getTotalElements(), userId, repostSavesByUser.getTotalPages(), repostSavesByUser.getTotalElements());
        return ResponseEntity.ok(repostSavesByUser);
    }

    @GetMapping("/user/action")
    public ResponseEntity<Page<RepostSaveDTO>> fetchRepostSavesByUserIdAndActionType(
            @RequestParam Long userId,
            @RequestParam ActionType actionType,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Started fetching repost/save actions for userId: {} and actionType: {} with pagination: page {} size {}",
                userId, actionType, pageable.getPageNumber(), pageable.getPageSize());
        Page<RepostSaveDTO> repostSavesByUserAndActionType = repostSaveService.getByUserIdAndActionType(userId, actionType, pageable);
        logger.info("Successfully fetched {} repost/save actions for userId: {} and actionType: {}. Total pages: {}, Total elements: {}",
                repostSavesByUserAndActionType.getTotalElements(), userId, actionType, repostSavesByUserAndActionType.getTotalPages(),
                repostSavesByUserAndActionType.getTotalElements());
        return ResponseEntity.ok(repostSavesByUserAndActionType);
    }

    @GetMapping("/user/content")
    public ResponseEntity<RepostSaveDTO> fetchRepostSaveByUserIdAndContent(
            @RequestParam Long userId,
            @RequestParam(required = false) Long reviewId,
            @RequestParam(required = false) Long quoteId,
            @RequestParam(required = false) Long bookInteractionId) {
        logger.info("Received request to fetch repost/save action for userId: {} with reviewId: {}, quoteId: {}, bookInteractionId: {}",
                userId, reviewId, quoteId, bookInteractionId);
        RepostSaveDTO repostSave = repostSaveService.getByUserIdAndContent(userId, reviewId, quoteId, bookInteractionId);
        logger.info("Successfully fetched repost/save for userId: {} with reviewId: {}, quoteId: {}, bookInteractionId: {}",
                userId, reviewId, quoteId, bookInteractionId);
        return ResponseEntity.ok(repostSave);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkIfRepostSaveExists(
            @RequestParam Long userId,
            @RequestParam(required = false) Long reviewId,
            @RequestParam(required = false) Long quoteId,
            @RequestParam(required = false) Long bookInteractionId,
            @RequestParam(required = false) String actionType) {
        logger.info("Received request to check if repost/save action exists for userId: {} with reviewId: {}, quoteId: {}, bookInteractionId: {}, and actionType: {}",
                userId, reviewId, quoteId, bookInteractionId, actionType);
        ActionType actionTypeEnum = ActionType.fromString(actionType);
        logger.info("Converting actionType: '{}' to enum: {}", actionType, actionTypeEnum);
        boolean exists = repostSaveService.existsByUserIdAndContentAndActionType(userId, reviewId, quoteId, bookInteractionId, actionTypeEnum);
        logger.info("Repost/save action check result for userId: {} with reviewId: {}, quoteId: {}, bookInteractionId: {}, actionType: {}: {}",
                userId, reviewId, quoteId, bookInteractionId, actionTypeEnum, exists);
        return ResponseEntity.ok(exists);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RepostSaveDTO> updateRepostSave(@PathVariable Long id, @Valid @RequestBody RepostSaveDTO repostSaveDTO) {
        logger.info("Received request to update repost/save action with id: {}", id);
        RepostSaveDTO updatedRepostSave = repostSaveService.modifyRepostSave(id, repostSaveDTO);
        logger.info("Successfully updated repost/save action with id: {}", id);
        return ResponseEntity.ok(updatedRepostSave);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRepostSave(@PathVariable Long id) {
        logger.info("Received request to delete repost-save with ID: {}", id);
        repostSaveService.removeRepostSave(id);
        logger.info("Successfully deleted repost/save action with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}