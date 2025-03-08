package com.batubook.backend.mapper;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.MessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageDTO messageEntityToMessageDTO(MessageEntity messageEntity);

    MessageEntity messageDTOToMessageEntity(MessageDTO messageDTO);
}