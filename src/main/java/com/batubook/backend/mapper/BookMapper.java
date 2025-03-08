package com.batubook.backend.mapper;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { QuoteMapper.class, ReviewMapper.class })
public interface BookMapper {

    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "publishDate", expression = "java(String.valueOf(bookEntity.getPublishDate().getYear()))")
    BookDTO bookEntityToBookDTO(BookEntity bookEntity);

    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "publishDate", expression = "java(java.time.LocalDate.of(Integer.parseInt(bookDTO.getPublishDate()), 1, 1))")
    BookEntity bookDTOToBookEntity(BookDTO bookDTO);

    default Genre mapStringToGenre(String genreString) {
        return genreString != null ? Genre.valueOf(genreString.toUpperCase()) : null;
    }

    default String mapGenreToString(Genre genre) {
        return genre != null ? genre.name() : null;
    }
}