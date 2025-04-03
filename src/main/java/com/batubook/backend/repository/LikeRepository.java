package com.batubook.backend.repository;

import com.batubook.backend.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    boolean existsByUserIdAndMessageId(Long userId, Long messageId);
    boolean existsByUserIdAndBookInteractionId(Long userId, Long bookInteractionId);
    boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
    boolean existsByUserIdAndQuoteId(Long userId, Long quoteId);
}