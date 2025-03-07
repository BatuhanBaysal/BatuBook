package com.batubook.backend.mapper;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.entity.QuoteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    QuoteDTO quoteEntityToQuoteDTO(QuoteEntity quoteEntity);

    QuoteEntity quoteDTOToQuoteEntity(QuoteDTO quoteDTO);
}