package com.batubook.backend.mapper;

import com.batubook.backend.dto.LikeDTO;
import com.batubook.backend.entity.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "message.id", source = "messageId")
    @Mapping(target = "bookInteraction.id", source = "bookInteractionId")
    @Mapping(target = "review.id", source = "reviewId")
    @Mapping(target = "quote.id", source = "quoteId")
    LikeEntity likeDTOToEntity(LikeDTO likeDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "messageId", source = "message.id")
    @Mapping(target = "bookInteractionId", source = "bookInteraction.id")
    @Mapping(target = "reviewId", source = "review.id")
    @Mapping(target = "quoteId", source = "quote.id")
    LikeDTO likeEntityToDTO(LikeEntity likeEntity);
}