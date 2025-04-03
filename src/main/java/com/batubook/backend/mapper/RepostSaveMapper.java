package com.batubook.backend.mapper;

import com.batubook.backend.dto.RepostSaveDTO;
import com.batubook.backend.entity.RepostSaveEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RepostSaveMapper {

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "bookInteractionId", target = "bookInteraction.id")
    @Mapping(source = "reviewId", target = "review.id")
    @Mapping(source = "quoteId", target = "quote.id")
    @Mapping(target = "actionType", source = "actionType")
    RepostSaveEntity repostSaveDTOToEntity(RepostSaveDTO repostSaveDTO);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "bookInteraction.id", target = "bookInteractionId")
    @Mapping(source = "review.id", target = "reviewId")
    @Mapping(source = "quote.id", target = "quoteId")
    @Mapping(target = "actionType", source = "actionType")
    RepostSaveDTO repostSaveEntityToDTO(RepostSaveEntity repostSaveEntity);
}