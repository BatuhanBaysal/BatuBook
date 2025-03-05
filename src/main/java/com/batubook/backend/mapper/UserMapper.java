package com.batubook.backend.mapper;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        uses = { UserProfileMapper.class, QuoteMapper.class, ReviewMapper.class, MessageMapper.class })
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", source = "role.name")
    UserDTO userEntityToUserDTO(UserEntity userEntity);

    @Mapping(target = "role", source = "role")
    UserEntity userDTOToUserEntity(UserDTO userDTO);

    default Role mapStringToRole(String roleString) {
        return roleString != null ? Role.valueOf(roleString.toUpperCase()) : null;
    }

    default String mapRoleToString(Role role) {
        return role != null ? role.name().toLowerCase() : null;
    }
}