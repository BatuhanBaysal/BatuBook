package com.batubook.backend.mapper;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
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

    default Gender mapStringToGender(String genderString) {
        return genderString != null ? Gender.valueOf(genderString.toUpperCase()) : null;
    }

    default String mapGenderToString(Gender gender) {
        return gender != null ? gender.name() : null;
    }
}