package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.LikeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeServiceInterface {

    LikeDTO registerLike(LikeDTO likeDTO);
    LikeDTO getLikeById(Long id);
    Page<LikeDTO> getAllLikes(Pageable pageable);
    boolean getByUserIdAndMessageId(Long userId, Long messageId);
    boolean getByUserIdAndBookInteractionId(Long userId, Long bookInteractionId);
    boolean getByUserIdAndReviewId(Long userId, Long reviewId);
    boolean getByUserIdAndQuoteId(Long userId, Long quoteId);
    LikeDTO modifyLike(Long id, LikeDTO likeDTO);
    void removeLike(Long id);
}