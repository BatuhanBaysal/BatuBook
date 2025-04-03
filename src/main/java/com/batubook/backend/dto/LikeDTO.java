package com.batubook.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {

    private Long id;
    private Long userId;
    private Long messageId;
    private Long bookInteractionId;
    private Long reviewId;
    private Long quoteId;
}