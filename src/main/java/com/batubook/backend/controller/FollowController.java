package com.batubook.backend.controller;

import com.batubook.backend.dto.FollowDTO;
import com.batubook.backend.service.serviceImplementation.FollowServiceImpl;
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
    public ResponseEntity<FollowDTO> followUser(@RequestBody FollowDTO followDTO) {
        logger.info("Request to follow user with ID: {}", followDTO.getFollowedUserId());
        FollowDTO result = followService.followUser(followDTO);
        logger.info("User with ID: {} followed successfully.", followDTO.getFollowedUserId());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/follow-book")
    public ResponseEntity<FollowDTO> followBook(@RequestBody FollowDTO followDTO) {
        logger.info("Request to follow book with ID: {}", followDTO.getFollowedBookId());
        FollowDTO result = followService.followBook(followDTO);
        logger.info("Book with ID: {} followed successfully.", followDTO.getFollowedBookId());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/unfollow-user")
    public ResponseEntity<Void> unfollowUser(@RequestBody FollowDTO followDTO) {
        logger.info("Request to unfollow user with ID: {}", followDTO.getFollowedUserId());
        boolean unfollowed = followService.unfollowUser(followDTO);
        if (unfollowed) {
            logger.info("User with ID: {} unfollowed successfully.", followDTO.getFollowedUserId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("User with ID: {} was not found for unfollow.", followDTO.getFollowedUserId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/unfollow-book")
    public ResponseEntity<Void> unfollowBook(@RequestBody FollowDTO followDTO) {
        logger.info("Request to unfollow book with ID: {}", followDTO.getFollowedBookId());
        boolean unfollowed = followService.unfollowBook(followDTO);
        if (unfollowed) {
            logger.info("Book with ID: {} unfollowed successfully.", followDTO.getFollowedBookId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Book with ID: {} was not found for unfollow.", followDTO.getFollowedBookId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/followed-users/{followerId}")
    public ResponseEntity<Page<FollowDTO>> getFollowedUsers(
            @PathVariable Long followerId,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Request to get followed users for follower with ID: {}", followerId);
        Page<FollowDTO> followedUsers = followService.getFollowedUsers(followerId, pageable);
        logger.info("Returning {} followed users for follower with ID: {}", followedUsers.getTotalElements(), followerId);
        return new ResponseEntity<>(followedUsers, HttpStatus.OK);
    }

    @GetMapping("/followers/{followedUserId}")
    public ResponseEntity<Page<FollowDTO>> getFollowers(
            @PathVariable Long followedUserId,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Request to get followers for followed user with ID: {}", followedUserId);
        Page<FollowDTO> followers = followService.getFollowers(followedUserId, pageable);
        logger.info("Returning {} followers for user with ID: {}", followers.getTotalElements(), followedUserId);
        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @GetMapping("/book-followers/{followedBookId}")
    public ResponseEntity<Page<FollowDTO>> getBookFollowers(
            @PathVariable Long followedBookId,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Request to get followers for book with ID: {}", followedBookId);
        Page<FollowDTO> bookFollowers = followService.getBookFollowers(followedBookId, pageable);
        logger.info("Returning {} followers for book with ID: {}", bookFollowers.getTotalElements(), followedBookId);
        return new ResponseEntity<>(bookFollowers, HttpStatus.OK);
    }
}