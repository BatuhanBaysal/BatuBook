package com.batubook.backend.mapper;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { UserProfileMapper.class, QuoteMapper.class, ReviewMapper.class, MessageMapper.class })
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserDTO userEntityToDTO(UserEntity userEntity);

    @Mapping(target = "role", source = "role")
    UserEntity userDTOToEntity(UserDTO userDTO);
}