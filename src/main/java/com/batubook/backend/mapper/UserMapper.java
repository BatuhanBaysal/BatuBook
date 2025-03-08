package com.batubook.backend.mapper;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { UserProfileMapper.class, QuoteMapper.class, ReviewMapper.class, MessageMapper.class })
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", expression = "java(userEntity.getRole().name())")
    UserDTO userEntityToUserDTO(UserEntity userEntity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", expression = "java(mapStringToRole(userDTO.getRole()))")
    UserEntity userDTOToUserEntity(UserDTO userDTO);

    default Role mapStringToRole(String roleString) {
        try {
            return roleString != null ? Role.valueOf(roleString.toUpperCase()) : Role.USER;
        } catch (IllegalArgumentException e) {
            return Role.USER;
        }
    }

    default String mapRoleToString(Role role) {
        return role != null ? role.name().toUpperCase() : "USER";
    }
}