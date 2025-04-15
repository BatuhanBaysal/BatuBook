package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.UserProfileMapper;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.service.serviceInterface.UserProfileServiceInterface;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileServiceInterface {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileById(Long id) {
        logger.info("Attempting to retrieve user profile with ID: {}", id);
        UserProfileEntity userProfileById = userProfileRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User profile not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("User profile not found with ID: " + id);
                });

        logger.info("Successfully retrieved user profile with ID: {}", id);
        return userProfileMapper.userProfileEntityToDTO(userProfileById);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getAllUserProfiles(Pageable pageable) {
        logger.debug("Fetching all user profiles with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileEntity> allUserProfiles = userProfileRepository.findAll(pageable);
        logger.info("Successfully fetched {} user profiles", allUserProfiles.getNumberOfElements());
        return allUserProfiles.map(userProfileMapper::userProfileEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getUserProfilesByBirthDate(LocalDate dateOfBirth, Pageable pageable) {
        logger.info("Received request to fetch user profiles with date of birth: {} and pagination: page {}, size {}",
                dateOfBirth, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileEntity> profiles = userProfileRepository.findByDateOfBirth(dateOfBirth, pageable);
        logger.info("Successfully fetched {} user profiles with date of birth: {} on page {}.",
                profiles.getTotalElements(), dateOfBirth, pageable.getPageNumber());
        return profiles.map(userProfileMapper::userProfileEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getUserProfilesByGender(Gender gender, Pageable pageable) {
        logger.info("Fetching user profiles with gender: {} and pagination: page {}, size {}", gender, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileEntity> profiles = userProfileRepository.findByGender(gender, pageable);
        logger.info("Successfully fetched {} user profiles with gender: {} on page {}.", profiles.getTotalElements(), gender, pageable.getPageNumber());
        return profiles.map(userProfileMapper::userProfileEntityToDTO);
    }

    @Override
    @Transactional
    public UserProfileDTO modifyUserProfile(Long id, UserProfileDTO userProfileDTO) {
        logger.info("Modifying user profile with ID: {}", id);
        try {
            UserProfileEntity existingProfile = userProfileRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User profile not found for modify with ID: {}", id);
                        return new CustomExceptions.NotFoundException("User profile not found with ID: " + id);
                    });

            modifyUserProfileDetails(existingProfile, userProfileDTO);
            UserProfileEntity updatedUserProfile = userProfileRepository.save(existingProfile);
            logger.info("User profile updated successfully for ID: {}", id);
            return userProfileMapper.userProfileEntityToDTO(updatedUserProfile);

        } catch (CustomExceptions.NotFoundException | CustomExceptions.BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating User Profile: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("User Profile could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeUserProfile(Long id) {
        logger.info("Attempting to remove user profile with ID: {}", id);
        if (!userProfileRepository.existsById(id)) {
            logger.error("User profile with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("User profile not found with ID: " + id);
        }

        userProfileRepository.deleteById(id);
        logger.info("Successfully deleted user profile with ID: {}", id);
    }

    private void modifyUserProfileDetails(UserProfileEntity profile, UserProfileDTO userProfileDTO) {
        if (userProfileDTO.getDateOfBirth() != null) {
            try {
                LocalDate dateOfBirth = LocalDate.parse(userProfileDTO.getDateOfBirth());
                if (dateOfBirth.isAfter(LocalDate.now())) {
                    logger.error("Date of Birth cannot be in the future.");
                    throw new CustomExceptions.BadRequestException("Date of Birth cannot be in the future");
                }
                profile.setDateOfBirth(dateOfBirth);
                logger.info("Updated Date of Birth for user profile ID: {}", profile.getId());
            } catch (DateTimeParseException e) {
                logger.error("Invalid date format for Date of Birth.");
                throw new CustomExceptions.BadRequestException("Invalid date format for Date of Birth. Expected format: yyyy-MM-dd");
            }
        }

        if (userProfileDTO.getGender() != null) {
            profile.setGender(userProfileDTO.getGender());
            logger.info("Updated Gender for user profile ID: {}", profile.getId());
        }

        if (userProfileDTO.getBiography() != null && !userProfileDTO.getBiography().trim().isEmpty()) {
            profile.setBiography(userProfileDTO.getBiography());
            logger.info("Updated Biography for user profile ID: {}", profile.getId());
        } else if (userProfileDTO.getBiography() != null) {
            logger.error("Biography cannot be empty.");
            throw new CustomExceptions.BadRequestException("Biography cannot be empty");
        }

        if (userProfileDTO.getLocation() != null && !userProfileDTO.getLocation().trim().isEmpty()) {
            profile.setLocation(userProfileDTO.getLocation());
            logger.info("Updated Location for user profile ID: {}", profile.getId());
        } else if (userProfileDTO.getLocation() != null) {
            logger.error("Location cannot be empty.");
            throw new CustomExceptions.BadRequestException("Location cannot be empty");
        }

        if (userProfileDTO.getOccupation() != null && !userProfileDTO.getOccupation().trim().isEmpty()) {
            profile.setOccupation(userProfileDTO.getOccupation());
            logger.info("Updated Occupation for user profile ID: {}", profile.getId());
        } else if (userProfileDTO.getOccupation() != null) {
            logger.error("Occupation cannot be empty.");
            throw new CustomExceptions.BadRequestException("Occupation cannot be empty");
        }

        if (userProfileDTO.getEducation() != null && !userProfileDTO.getEducation().trim().isEmpty()) {
            profile.setEducation(userProfileDTO.getEducation());
            logger.info("Updated Education for user profile ID: {}", profile.getId());
        } else if (userProfileDTO.getEducation() != null) {
            logger.error("Education cannot be empty.");
            throw new CustomExceptions.BadRequestException("Education cannot be empty");
        }

        if (userProfileDTO.getProfileImageUrl() != null && !userProfileDTO.getProfileImageUrl().trim().isEmpty()) {
            profile.setProfileImageUrl(userProfileDTO.getProfileImageUrl());
            logger.info("Updated Image Url for user profile ID: {}", profile.getId());
        } else if (userProfileDTO.getProfileImageUrl() != null) {
            logger.error("Profile Image URL cannot be empty.");
            throw new CustomExceptions.BadRequestException("Profile Image URL cannot be empty");
        }

        if (userProfileDTO.getInterests() != null && !userProfileDTO.getInterests().trim().isEmpty()) {
            profile.setInterests(userProfileDTO.getInterests());
            logger.info("Updated Interests for user profile ID: {}", profile.getId());
        } else if (userProfileDTO.getInterests() != null) {
            logger.error("Interests cannot be empty.");
            throw new CustomExceptions.BadRequestException("Interests cannot be empty");
        }
    }
}