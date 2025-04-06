package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.RepostSaveDTO;
import com.batubook.backend.entity.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RepostSaveServiceInterface {

    RepostSaveDTO registerRepostSave(RepostSaveDTO repostSaveDTO);
    RepostSaveDTO getRepostSaveById(Long id);
    Page<RepostSaveDTO> getAllRepostSaves(Pageable pageable);
    Page<RepostSaveDTO> getByUserId(Long userId, Pageable pageable);
    Page<RepostSaveDTO> getByUserIdAndActionType(Long userId, ActionType actionType, Pageable pageable);
    RepostSaveDTO getByUserIdAndContent(Long userId, Long reviewId, Long quoteId, Long bookInteractionId);
    boolean existsByUserIdAndContentAndActionType(Long userId, Long reviewId, Long quoteId, Long bookInteractionId, ActionType actionType);
    RepostSaveDTO modifyRepostSave(Long id, RepostSaveDTO repostSaveDTO);
    void removeRepostSave(Long id);
}