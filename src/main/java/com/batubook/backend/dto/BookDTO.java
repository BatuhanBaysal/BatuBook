package com.batubook.backend.dto;

import com.batubook.backend.entity.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private int pageCount;
    private String publishDate;
    private Genre genre;
    private Set<ReviewDTO> reviews;
    private Set<QuoteDTO> quotes;
}