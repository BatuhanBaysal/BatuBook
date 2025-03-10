package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.mapper.UserProfileMapper;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.service.serviceInterface.UserProfileServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileServiceInterface {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Override
    public UserProfileDTO getUserProfileById(Long id) {
        try {
            UserProfileEntity userProfileEntity = userProfileRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User profile not found with ID: {}", id);
                        return new EntityNotFoundException("User profile not found with ID: " + id);
                    });

            logger.info("User profile retrieved successfully with ID: {}", id);
            return userProfileMapper.userProfileEntityToUserProfileDTO(userProfileEntity);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving user profile with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<UserProfileDTO> getAllUserProfiles() {
        try {
            List<UserProfileEntity> profiles = userProfileRepository.findAll();
            logger.info("Successfully fetched {} user profiles.", profiles.size());
            return profiles.stream().map(userProfileMapper::userProfileEntityToUserProfileDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all user profiles", e);
            throw e;
        }
    }

    @Override
    public List<UserProfileDTO> getUserProfilesByDateOfBirth(LocalDate dateOfBirth) {
        try {
            List<UserProfileEntity> profiles = userProfileRepository.findByDateOfBirth(dateOfBirth);
            logger.info("Successfully fetched {} user profiles with date of birth {}.", profiles.size(), dateOfBirth);
            return profiles.stream().map(userProfileMapper::userProfileEntityToUserProfileDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching user profiles by date of birth {}: {}", dateOfBirth, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<UserProfileDTO> getUserProfilesByGender(Gender gender, Pageable pageable) {
        try {
            Page<UserProfileEntity> profiles = userProfileRepository.findByGender(gender, pageable);
            logger.info("Successfully fetched {} user profiles with gender {}.", profiles.getSize(), gender);
            return profiles.map(userProfileMapper::userProfileEntityToUserProfileDTO);
        } catch (Exception e) {
            logger.error("Error fetching user profiles by gender {}: {}", gender, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserProfileDTO updateUserProfile(Long id, UserProfileDTO userProfileDTO) {
        try {
            UserProfileEntity existingProfile = userProfileRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User profile not found with ID: {}", id);
                        return new EntityNotFoundException("User profile not found with ID: " + id);
                    });

            updateUserProfileDetails(existingProfile, userProfileDTO);
            UserProfileEntity updatedUserProfile = userProfileRepository.save(existingProfile);
            logger.info("User profile updated successfully for ID: {}", id);
            return userProfileMapper.userProfileEntityToUserProfileDTO(updatedUserProfile);
        } catch (Exception e) {
            logger.error("Error updating user profile with ID: {}", id, e);
            throw e;
        }
    }

    private void updateUserProfileDetails(UserProfileEntity profile, UserProfileDTO userProfileDTO) {
        if (userProfileDTO.getDateOfBirth() != null) {
            profile.setDateOfBirth(LocalDate.parse(userProfileDTO.getDateOfBirth()));
            logger.info("Updated Date of Birth for user profile ID: {}", profile.getId());
        }
        if (userProfileDTO.getGender() != null) {
            profile.setGender(userProfileDTO.getGender());
            logger.info("Updated Gender for user profile ID: {}", profile.getId());
        }
        if (userProfileDTO.getBiography() != null) {
            profile.setBiography(userProfileDTO.getBiography());
            logger.info("Updated Biography for user profile ID: {}", profile.getId());
        }
        if (userProfileDTO.getLocation() != null) {
            profile.setLocation(userProfileDTO.getLocation());
            logger.info("Updated Location for user profile ID: {}", profile.getId());
        }
        if (userProfileDTO.getOccupation() != null) {
            profile.setOccupation(userProfileDTO.getOccupation());
            logger.info("Updated Occupation for user profile ID: {}", profile.getId());
        }
        if (userProfileDTO.getEducation() != null) {
            profile.setEducation(userProfileDTO.getEducation());
            logger.info("Updated Education for user profile ID: {}", profile.getId());
        }
    }

    @Override
    public void deleteUserProfileById(Long userId) {
        try {
            UserProfileEntity profile = userProfileRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User profile not found with ID: {}", userId);
                        return new EntityNotFoundException("User profile not found with ID: " + userId);
                    });

            userProfileRepository.delete(profile);
            logger.info("User profile deleted successfully with ID: {}", userId);
        } catch (Exception e) {
            logger.error("Error occurred while deleting user profile with ID: {}", userId, e);
            throw e;
        }
    }
}