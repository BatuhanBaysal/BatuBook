package com.batubook.backend.mapper;

import com.batubook.backend.dto.BookSalesDTO;
import com.batubook.backend.entity.BookSalesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookSalesMapper {

    @Mapping(source = "book.id", target = "bookId")
    BookSalesDTO bookSalesEntityToDTO(BookSalesEntity bookSalesEntity);

    @Mapping(source = "bookId", target = "book.id")
    BookSalesEntity bookSalesDTOToEntity(BookSalesDTO bookSalesDTO);
}