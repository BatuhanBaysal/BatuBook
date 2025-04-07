package com.batubook.backend.controller;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.service.serviceImplementation.FollowServiceImpl;
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
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowServiceImpl followService;
    private final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @PostMapping("/follow-user")
    public ResponseEntity<FollowDTO> followUser(@Valid @RequestBody FollowDTO followDTO) {
        logger.info("Request to follow user with ID: {}", followDTO.getFollowedUserId());
        FollowDTO followedUser = followService.followUser(followDTO);
        logger.info("User with ID: {} followed successfully.", followDTO.getFollowedUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(followedUser);
    }

    @PostMapping("/follow-book")
    public ResponseEntity<FollowDTO> followBook(@RequestBody FollowDTO followDTO) {
        logger.info("Request to follow book with ID: {}", followDTO.getFollowedBookId());
        FollowDTO followedBook = followService.followBook(followDTO);
        logger.info("Book with ID: {} followed successfully.", followDTO.getFollowedBookId());
        return ResponseEntity.status(HttpStatus.CREATED).body(followedBook);
    }

    @GetMapping
    public ResponseEntity<Page<FollowDTO>> fetchAllFollows(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("Fetching all follows with Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<FollowDTO> allFollows = followService.getAllFollows(pageable);
        logger.info("Fetched {} follow records.", allFollows.getTotalElements());
        return ResponseEntity.ok(allFollows);
    }

    @GetMapping("/followed-users/{followerId}")
    public ResponseEntity<Page<FollowDTO>> getFollowedUsers(
            @PathVariable Long followerId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Request to get followed users for follower with ID: {}", followerId);
        Page<FollowDTO> followedUsers = followService.getFollowedUsers(followerId, pageable);
        logger.info("Returning {} followed users for follower with ID: {}", followedUsers.getTotalElements(), followerId);
        return ResponseEntity.ok(followedUsers);
    }

    @GetMapping("/followers/{followedUserId}")
    public ResponseEntity<Page<FollowDTO>> getFollowers(
            @PathVariable Long followedUserId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Request to get followers for followed user with ID: {}", followedUserId);
        Page<FollowDTO> followers = followService.getFollowers(followedUserId, pageable);
        logger.info("Returning {} followers for user with ID: {}", followers.getTotalElements(), followedUserId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/book-followers/{followedBookId}")
    public ResponseEntity<Page<FollowDTO>> getBookFollowers(
            @PathVariable Long followedBookId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Request to get followers for book with ID: {}", followedBookId);
        Page<FollowDTO> bookFollowers = followService.getBookFollowers(followedBookId, pageable);
        logger.info("Returning {} followers for book with ID: {}", bookFollowers.getTotalElements(), followedBookId);
        return ResponseEntity.ok(bookFollowers);
    }

    @DeleteMapping("/unfollow-user")
    public ResponseEntity<Void> unfollowUser(@RequestBody FollowDTO followDTO) {
        logger.info("Request received to unfollow user with ID: {} by follower with ID: {}",
                followDTO.getFollowedUserId(), followDTO.getFollowerId());
        followService.unfollowUser(followDTO);
        logger.info("User with ID: {} successfully unfollowed user with ID: {}",
                followDTO.getFollowerId(), followDTO.getFollowedUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unfollow-book")
    public ResponseEntity<Void> unfollowBook(@RequestBody FollowDTO followDTO) {
        logger.info("Request received to unfollow book with ID: {} by user with ID: {}",
                followDTO.getFollowedBookId(), followDTO.getFollowerId());
        followService.unfollowBook(followDTO);
        logger.info("User with ID: {} successfully unfollowed book with ID: {}",
                followDTO.getFollowerId(), followDTO.getFollowedBookId());
        return ResponseEntity.noContent().build();
    }
}