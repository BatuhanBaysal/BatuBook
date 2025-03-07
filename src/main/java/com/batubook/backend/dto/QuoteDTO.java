package com.batubook.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteDTO {

    private Long id;
    private String quoteText;
    private Long userId;
    private Long bookId;
}