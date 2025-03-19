package com.batubook.backend.mapper;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    UserProfileDTO userProfileEntityToUserProfileDTO(UserProfileEntity userProfileEntity);

    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    UserProfileEntity userProfileDTOToUserProfileEntity(UserProfileDTO userProfileDTO);
}