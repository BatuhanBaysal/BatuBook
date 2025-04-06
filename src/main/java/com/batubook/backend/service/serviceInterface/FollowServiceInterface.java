package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.FollowDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowServiceInterface {

    FollowDTO followUser(FollowDTO followDTO);
    FollowDTO followBook(FollowDTO followDTO);
    Page<FollowDTO> getAllFollows(Pageable pageable);
    Page<FollowDTO> getFollowedUsers(Long followerId, Pageable pageable);
    Page<FollowDTO> getFollowers(Long followedUserId, Pageable pageable);
    Page<FollowDTO> getBookFollowers(Long followedBookId, Pageable pageable);
    void unfollowUser(FollowDTO followDTO);
    void unfollowBook(FollowDTO followDTO);
}