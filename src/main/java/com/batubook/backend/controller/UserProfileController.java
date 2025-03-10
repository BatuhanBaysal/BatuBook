package com.batubook.backend.controller;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.service.serviceImplementation.UserProfileServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/userProfiles")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @GetMapping("/userProfileId/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable Long id) {
        try {
            logger.info("Fetching user with ID: {}", id);
            UserProfileDTO userProfileDTO = userProfileService.getUserProfileById(id);
            logger.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(userProfileDTO);
        } catch (EntityNotFoundException e) {
            logger.error("User with ID: {} not found.", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/allUserProfiles")
    public ResponseEntity<List<UserProfileDTO>> getAllUserProfiles() {
        try {
            logger.info("Fetching all user profiles.");
            List<UserProfileDTO> userProfiles = userProfileService.getAllUserProfiles();
            logger.info("Successfully fetched {} user profiles.", userProfiles.size());
            return ResponseEntity.ok(userProfiles);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all user profiles.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search-birthday")
    public ResponseEntity<List<UserProfileDTO>> getUserProfilesByBirthday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth) {
        try {
            logger.info("Fetching user profiles for birthday: {}", dateOfBirth);
            List<UserProfileDTO> userProfiles = userProfileService.getUserProfilesByDateOfBirth(dateOfBirth);
            logger.info("Successfully fetched {} user profiles for birthday: {}", userProfiles.size(), dateOfBirth);
            return ResponseEntity.ok(userProfiles);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user profiles for birthday: {}", dateOfBirth, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search-gender")
    public ResponseEntity<Page<UserProfileDTO>> getUserProfilesByGender(
            @RequestParam String gender,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            Gender genderEnum = Gender.valueOf(gender.toUpperCase());
            logger.info("Fetching user profiles for gender: {}", genderEnum);
            Page<UserProfileDTO> userProfiles = userProfileService.getUserProfilesByGender(genderEnum, pageable);
            logger.info("Successfully fetched {} user profiles for gender: {}", userProfiles.getSize(), genderEnum);
            return ResponseEntity.ok(userProfiles);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid gender value: {}. Please use 'MALE' or 'FEMALE'.", gender, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            logger.error("Error occurred while fetching user profiles for gender: {}", gender, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updateUserProfile/{id}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserProfileDTO userProfileDTO) {
        try {
            logger.info("Updating user profile with ID: {}", id);
            UserProfileDTO updatedUserProfile = userProfileService.updateUserProfile(id, userProfileDTO);
            logger.info("Successfully updated user profile with ID: {}", id);
            return ResponseEntity.ok(updatedUserProfile);
        } catch (EntityNotFoundException e) {
            logger.error("User with ID: {} not found for update.", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error occurred while updating user profile with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/deleteUserProfile/{userId}")
    public ResponseEntity<UserProfileDTO> deleteUserProfile(@PathVariable Long userId) {
        try {
            logger.info("Deleting user with ID: {}", userId);
            userProfileService.deleteUserProfileById(userId);
            logger.info("Successfully deleted user with ID: {}", userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            logger.error("User with ID: {} not found for deletion.", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}