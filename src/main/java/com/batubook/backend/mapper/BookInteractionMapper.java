package com.batubook.backend.mapper;

import com.batubook.backend.dto.BookInteractionDTO;
import com.batubook.backend.entity.BookInteractionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { MessageMapper.class, LikeMapper.class, RepostSaveMapper.class })
public interface BookInteractionMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    BookInteractionDTO bookInteractionEntityToDTO(BookInteractionEntity bookInteractionEntity);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "bookId", target = "book.id")
    BookInteractionEntity bookInteractionDTOToEntity(BookInteractionDTO bookInteractionDTO);
}