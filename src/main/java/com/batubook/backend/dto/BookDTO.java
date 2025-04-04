package com.batubook.backend.dto;

import com.batubook.backend.entity.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;
    private String bookCoverImageUrl;
    private String title;
    private String author;
    private String isbn;
    private Integer pageCount;
    private String publishDate;
    private Genre genre;
    private String summary;
}