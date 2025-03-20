package com.batubook.backend.repository;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    FollowEntity findByFollowerAndFollowedUser(UserEntity follower, UserEntity followedUser);
    FollowEntity findByFollowerAndFollowedBook(UserEntity follower, BookEntity followedBook);
    Page<FollowEntity> findByFollower(UserEntity follower, Pageable pageable);
    Page<FollowEntity> findByFollowedUser(UserEntity followedUser, Pageable pageable);
    Page<FollowEntity> findByFollowedBook(BookEntity followedBook, Pageable pageable);
}