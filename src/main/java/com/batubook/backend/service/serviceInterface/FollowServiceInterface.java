package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.FollowDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowServiceInterface {

    FollowDTO followUser(FollowDTO followDTO);
    FollowDTO followBook(FollowDTO followDTO);
    boolean unfollowUser(FollowDTO followDTO);
    boolean unfollowBook(FollowDTO followDTO);
    Page<FollowDTO> getFollowedUsers(Long followerId, Pageable pageable);
    Page<FollowDTO> getFollowers(Long followedUserId, Pageable pageable);
    Page<FollowDTO> getBookFollowers(Long followedBookId, Pageable pageable);
}