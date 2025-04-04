package com.batubook.backend.mapper;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { BookSalesMapper.class, BookInteractionMapper.class, QuoteMapper.class, ReviewMapper.class, FollowMapper.class })
public interface BookMapper {

    @Mapping(target = "publishDate", expression = "java(bookEntity.getPublishDate().toString())")
    BookDTO bookEntityToDTO(BookEntity bookEntity);

    @Mapping(target = "publishDate", expression = "java(java.time.LocalDate.parse(bookDTO.getPublishDate()))")
    BookEntity bookDTOToEntity(BookDTO bookDTO);
}