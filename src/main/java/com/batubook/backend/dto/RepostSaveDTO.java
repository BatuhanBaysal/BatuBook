package com.batubook.backend.dto;

import com.batubook.backend.entity.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepostSaveDTO {

    private Long id;
    private ActionType actionType;
    private Long userId;
    private Long bookInteractionId;
    private Long reviewId;
    private Long quoteId;
}