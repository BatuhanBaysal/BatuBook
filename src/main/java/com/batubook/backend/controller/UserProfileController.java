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
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> fetchUserProfileById(@PathVariable Long id) {
        logger.info("Received GET request for /api/user-profiles/{}", id);
        UserProfileDTO userProfileDTO = userProfileService.getUserProfileById(id);
        logger.info("Successfully retrieved user profile with ID: {}", id);
        return ResponseEntity.ok(userProfileDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UserProfileDTO>> fetchAllUserProfiles(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/user-profiles called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileDTO> allUserProfiles = userProfileService.getAllUserProfiles(pageable);
        logger.info("Successfully fetched {} user profiles", allUserProfiles.getSize());
        return ResponseEntity.ok(allUserProfiles);
    }

    @GetMapping("/search-birthday")
    public ResponseEntity<Page<UserProfileDTO>> fetchUserProfilesByBirthday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch user profiles for birthday: {} with pagination: page {}, size {}",
                dateOfBirth, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileDTO> userProfiles = userProfileService.getUserProfilesByBirthDate(dateOfBirth, pageable);
        logger.info("Successfully fetched {} user profiles for birthday: {} on page {}.",
                userProfiles.getTotalElements(), dateOfBirth, pageable.getPageNumber());
        return ResponseEntity.ok(userProfiles);
    }

    @GetMapping("/search-gender")
    public ResponseEntity<Page<UserProfileDTO>> fetchUserProfilesByGender(
            @RequestParam String gender,
            @PageableDefault(size = 5) Pageable pageable) {
        Gender genderEnum = Gender.fromString(gender);
        logger.info("Received request to fetch user profiles for gender: {} with pagination: page {}, size {}",
                genderEnum, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileDTO> userProfiles = userProfileService.getUserProfilesByGender(genderEnum, pageable);
        logger.info("Successfully fetched {} user profiles for gender: {} on page {}.",
                userProfiles.getTotalElements(), genderEnum, pageable.getPageNumber());
        return ResponseEntity.ok(userProfiles);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfileDTO userProfileDTO) {
        logger.info("Updating user profile with ID: {}", id);
        UserProfileDTO updatedUserProfile = userProfileService.modifyUserProfile(id, userProfileDTO);
        logger.info("Successfully updated user profile with ID: {}", id);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        logger.info("Received request to delete user profile with ID: {}", id);
        userProfileService.removeUserProfile(id);
        logger.info("Successfully deleted user profile with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}