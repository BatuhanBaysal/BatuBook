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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileServiceInterface {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileById(Long id) {
        logger.info("Fetching user profile with ID: {}", id);

        try {
            UserProfileEntity userProfileEntity = userProfileRepository.findById(id)
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("User profile not found with ID: " + id));

            logger.info("User profile retrieved successfully with ID: {}", id);
            return userProfileMapper.userProfileEntityToUserProfileDTO(userProfileEntity);

        } catch (Exception e) {
            logger.error("Error occurred while retrieving user profile with ID: {}", id, e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching the user profile.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getAllUserProfiles(Pageable pageable) {
        logger.info("Fetching all user profiles");

        try {
            Page<UserProfileEntity> profiles = userProfileRepository.findAll(pageable);
            logger.info("Successfully fetched {} user profiles.", profiles.getSize());
            return profiles.map(userProfileMapper::userProfileEntityToUserProfileDTO);

        } catch (Exception e) {
            logger.error("Error fetching all user profiles", e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching all user profiles.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getUserProfilesByDateOfBirth(LocalDate dateOfBirth, Pageable pageable) {
        logger.info("Fetching user profiles with date of birth: {}", dateOfBirth);

        try {
            Page<UserProfileEntity> profiles = userProfileRepository.findByDateOfBirth(dateOfBirth, pageable);
            logger.info("Successfully fetched {} user profiles with date of birth {}", profiles.getSize(), dateOfBirth);
            return profiles.map(userProfileMapper::userProfileEntityToUserProfileDTO);

        } catch (Exception e) {
            logger.error("Error fetching user profiles by date of birth {}: {}", dateOfBirth, e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching user profiles.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getUserProfilesByGender(Gender gender, Pageable pageable) {
        logger.info("Fetching user profiles with gender: {} and pagination: {}", gender, pageable);

        try {
            Page<UserProfileEntity> profiles = userProfileRepository.findByGender(gender, pageable);
            logger.info("Successfully fetched {} user profiles with gender {}.", profiles.getSize(), gender);
            return profiles.map(userProfileMapper::userProfileEntityToUserProfileDTO);

        } catch (Exception e) {
            logger.error("Error fetching user profiles by gender {}: {}", gender, e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching user profiles.");
        }
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
            return userProfileMapper.userProfileEntityToUserProfileDTO(updatedUserProfile);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("User profile not found with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user profile with ID: {}", id, e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while updating the user profile.");
        }
    }

    private void modifyUserProfileDetails(UserProfileEntity profile, UserProfileDTO userProfileDTO) {
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
    @Transactional
    public void removeUserProfileById(Long userId) {
        logger.info("Deleting user with ID: {}", userId);

        try {
            userProfileRepository.deleteById(userId);
            logger.info("User deleted successfully with ID: {}", userId);

        } catch (EmptyResultDataAccessException e) {
            logger.error("User not found for delete with ID: {}", userId, e);
            throw new CustomExceptions.NotFoundException("User not found with ID: " + userId);
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with ID: {}", userId, e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while deleting the user.");
        }
    }
}