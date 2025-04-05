package com.batubook.backend.mapper;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.entity.QuoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { RepostSaveMapper.class, LikeMapper.class })
public interface QuoteMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    QuoteDTO quoteEntityToQuoteDTO(QuoteEntity quoteEntity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "bookId", target = "book.id")
    QuoteEntity quoteDTOToQuoteEntity(QuoteDTO quoteDTO);
}