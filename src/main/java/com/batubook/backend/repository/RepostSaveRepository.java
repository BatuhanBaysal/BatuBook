package com.batubook.backend.repository;

import com.batubook.backend.entity.RepostSaveEntity;
import com.batubook.backend.entity.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepostSaveRepository extends JpaRepository<RepostSaveEntity, Long> {

    Page<RepostSaveEntity> findByUserId(Long userId, Pageable pageable);
    Page<RepostSaveEntity> findByUserIdAndActionType(Long userId, ActionType actionType, Pageable pageable);

    @Query("""
        SELECT r FROM RepostSaveEntity r
        WHERE r.user.id = :userId
        AND (:reviewId IS NULL OR r.review.id = :reviewId)
        AND (:quoteId IS NULL OR r.quote.id = :quoteId)
        AND (:bookInteractionId IS NULL OR r.bookInteraction.id = :bookInteractionId)
        """)
    Optional<RepostSaveEntity> findByUserIdAndContent(
            @Param("userId") Long userId,
            @Param("reviewId") Long reviewId,
            @Param("quoteId") Long quoteId,
            @Param("bookInteractionId") Long bookInteractionId
    );

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM RepostSaveEntity r
        WHERE r.user.id = :userId
        AND (:reviewId IS NULL OR r.review.id = :reviewId)
        AND (:quoteId IS NULL OR r.quote.id = :quoteId)
        AND (:bookInteractionId IS NULL OR r.bookInteraction.id = :bookInteractionId)
        AND r.actionType = :actionType
        """)
    boolean existsByUserIdAndContentAndActionType(
            @Param("userId") Long userId,
            @Param("reviewId") Long reviewId,
            @Param("quoteId") Long quoteId,
            @Param("bookInteractionId") Long bookInteractionId,
            @Param("actionType") ActionType actionType
    );
}