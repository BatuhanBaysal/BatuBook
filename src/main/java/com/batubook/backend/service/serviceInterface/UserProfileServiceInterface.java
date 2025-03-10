package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface UserProfileServiceInterface {

    UserProfileDTO getUserProfileById(Long id);
    List<UserProfileDTO> getAllUserProfiles();
    List<UserProfileDTO> getUserProfilesByDateOfBirth(LocalDate dateOfBirth);
    Page<UserProfileDTO> getUserProfilesByGender(Gender gender, Pageable pageable);
    UserProfileDTO updateUserProfile(Long id, UserProfileDTO userProfileDTO);
    void deleteUserProfileById(Long userId);
}