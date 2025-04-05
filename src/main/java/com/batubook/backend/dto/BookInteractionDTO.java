package com.batubook.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookInteractionDTO {

    private Long id;
    private String description;
    private Boolean isRead;
    private Boolean isLiked;
    private Long userId;
    private Long bookId;
}