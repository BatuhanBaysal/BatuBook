package com.batubook.backend.mapper;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.MessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = { LikeMapper.class })
public interface MessageMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "review.id", target = "reviewId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "quote.id", target = "quoteId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "bookInteraction.id", target = "interactionId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "messageType", target = "messageType")
    MessageDTO messageEntityToDTO(MessageEntity messageEntity);

    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "review", ignore = true)
    @Mapping(target = "quote", ignore = true)
    @Mapping(target = "bookInteraction", ignore = true)
    @Mapping(target = "messageType", ignore = true)
    MessageEntity messageDTOToEntity(MessageDTO messageDTO);
}