package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserProfileServiceInterface {

    UserProfileDTO getUserProfileById(Long id);
    Page<UserProfileDTO> getAllUserProfiles(Pageable pageable);
    Page<UserProfileDTO> getUserProfilesByBirthDate(LocalDate dateOfBirth, Pageable pageable);
    Page<UserProfileDTO> getUserProfilesByGender(Gender gender, Pageable pageable);
    UserProfileDTO modifyUserProfile(Long id, UserProfileDTO userProfileDTO);
    void removeUserProfile(Long id);
}