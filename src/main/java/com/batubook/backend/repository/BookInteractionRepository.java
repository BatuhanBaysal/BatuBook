package com.batubook.backend.repository;

import com.batubook.backend.entity.BookInteractionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInteractionRepository extends JpaRepository<BookInteractionEntity, Long> {

    Page<BookInteractionEntity> findByUserIdAndIsReadTrue(Long userId, Pageable pageable);
    Page<BookInteractionEntity> findByUserIdAndIsLikedTrue(Long userId, Pageable pageable);
    Page<BookInteractionEntity> findByBookIdAndIsReadTrue(Long bookId, Pageable pageable);
    Page<BookInteractionEntity> findByBookIdAndIsLikedTrue(Long bookId, Pageable pageable);
    boolean existsByUserIdAndBookIdAndIsReadTrue(Long userId, Long bookId);
    boolean existsByUserIdAndBookIdAndIsLikedTrue(Long userId, Long bookId);
}