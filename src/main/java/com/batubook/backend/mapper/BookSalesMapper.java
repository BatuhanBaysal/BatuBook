package com.batubook.backend.mapper;

import com.batubook.backend.dto.BookSalesDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.BookSalesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BookSalesMapper {

    @Mapping(source = "book.id", target = "bookId")
    BookSalesDTO bookSalesEntityToDTO(BookSalesEntity bookSalesEntity);

    @Mapping(source = "bookId", target = "book", qualifiedByName = "mapBookIdToBook")
    BookSalesEntity bookSalesDTOToEntity(BookSalesDTO bookSalesDTO);

    @Named("mapBookIdToBook")
    default BookEntity mapBookIdToBook(Long bookId) {
        if (bookId == null) {
            return null;
        }
        BookEntity bookEntity = new BookEntity();
        bookEntity.setId(bookId);
        return bookEntity;
    }
}