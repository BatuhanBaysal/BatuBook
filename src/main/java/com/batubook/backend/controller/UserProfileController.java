package com.batubook.backend.controller;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.service.serviceImplementation.UserProfileServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/userProfiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @GetMapping("/userProfileId/{id}")
    public ResponseEntity<UserProfileDTO> fetchUserProfileById(@PathVariable Long id) {
        logger.info("Fetching user profile with ID: {}", id);
        UserProfileDTO userProfileDTO = userProfileService.getUserProfileById(id);
        logger.info("Successfully retrieved user profile with ID: {}", id);
        return ResponseEntity.ok(userProfileDTO);
    }

    @GetMapping("/allUserProfiles")
    public ResponseEntity<Page<UserProfileDTO>> fetchAllUserProfiles(@PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching all user profiles");
        Page<UserProfileDTO> userProfiles = userProfileService.getAllUserProfiles(pageable);
        logger.info("Successfully fetched {} user profiles", userProfiles.getSize());
        return ResponseEntity.ok(userProfiles);
    }

    @GetMapping("/search-birthday")
    public ResponseEntity<Page<UserProfileDTO>> fetchUserProfilesByBirthday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching user profiles for birthday: {}", dateOfBirth);
        Page<UserProfileDTO> userProfiles = userProfileService.getUserProfilesByDateOfBirth(dateOfBirth, pageable);
        logger.info("Successfully fetched {} user profiles for birthday: {}", userProfiles.getSize(), dateOfBirth);
        return ResponseEntity.ok(userProfiles);
    }

    @GetMapping("/search-gender")
    public ResponseEntity<Page<UserProfileDTO>> fetchUserProfilesByGender(
            @RequestParam String gender,
            @PageableDefault(size = 10) Pageable pageable) {
        Gender genderEnum = Gender.fromString(gender);
        logger.info("Fetching user profiles for gender: {}", genderEnum);
        Page<UserProfileDTO> userProfiles = userProfileService.getUserProfilesByGender(genderEnum, pageable);
        logger.info("Successfully fetched {} user profiles for gender: {}", userProfiles.getSize(), genderEnum);
        return ResponseEntity.ok(userProfiles);
    }

    @PutMapping("/updateUserProfile/{id}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileDTO userProfileDTO) {
        logger.info("Updating user profile with ID: {}", id);
        UserProfileDTO updatedUserProfile = userProfileService.modifyUserProfile(id, userProfileDTO);
        logger.info("Successfully updated user profile with ID: {}", id);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @DeleteMapping("/deleteUserProfile/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        logger.info("Deleting user profile with ID: {}", id);
        userProfileService.removeUserProfileById(id);
        logger.info("Successfully deleted user profile with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}