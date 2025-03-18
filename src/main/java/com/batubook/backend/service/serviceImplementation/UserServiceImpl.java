package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.exception.CustomException;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.UserMapper;
import com.batubook.backend.mapper.UserProfileMapper;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.UserServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        logger.info("Starting user creation process for username: {}", userDTO.getUsername());

        try {
            validateUserDTO(userDTO);

            UserEntity userEntity = userMapper.userDTOToUserEntity(userDTO);
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            UserProfileEntity userProfileEntity = mapUserProfile(userDTO);
            userProfileEntity.setUser(userEntity);
            userEntity.setUserProfile(userProfileEntity);

            UserEntity savedUser = userRepository.save(userEntity);
            logger.info("User created successfully: {}", userDTO.getUsername());
            return userMapper.userEntityToUserDTO(savedUser);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad request while creating user: {}", userDTO.getUsername(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while creating user: {}", userDTO.getUsername(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while creating the user.");
        }
    }

    private void validateUserDTO(UserDTO userDTO) {
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            logger.error("Password is empty for user: {}", userDTO.getUsername());
            throw new CustomExceptions.BadRequestException("Password cannot be empty.");
        }

        if (userDTO.getUserProfile() == null) {
            logger.error("User profile is missing for user: {}", userDTO.getUsername());
            throw new CustomExceptions.BadRequestException("User profile is required.");
        }
    }

    private UserProfileEntity mapUserProfile(UserDTO userDTO) {
        Gender genderEnum = Gender.fromString(userDTO.getUserProfile().getGender().toString());
        userDTO.getUserProfile().setGender(genderEnum);
        return userProfileMapper.userProfileDTOToUserProfileEntity(userDTO.getUserProfile());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);

        try {
            UserEntity userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("User not found with ID: " + id));

            logger.info("User retrieved successfully with ID: {}", id);
            return userMapper.userEntityToUserDTO(userEntity);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("User not found for ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while retrieving user with ID: {}", id, e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching the user.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        logger.info("Fetching all users with pagination: {}", pageable);

        try {
            Page<UserEntity> users = userRepository.findAll(pageable);
            if (users.isEmpty()) {
                logger.warn("No users found for the given pagination parameters");
                throw new CustomExceptions.NotFoundException("No users found.");
            }

            logger.info("Successfully fetched {} users", users.getSize());
            return users.map(userMapper::userEntityToUserDTO);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("No users found", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching all users: {}", e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching all users.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByRole(Role role, Pageable pageable) {
        logger.info("Fetching users with role: {} and pagination: {}", role, pageable);

        if (role == null) {
            logger.error("Role is null");
            throw new CustomExceptions.BadRequestException("Role must be provided.");
        }

        try {
            Page<UserEntity> users = userRepository.findByRole(role, pageable);
            if (users.isEmpty()) {
                logger.warn("No users found for role: {}", role);
                throw new CustomExceptions.NotFoundException("No users found with the provided role.");
            }

            logger.info("Successfully fetched {} users with role {}", users.getSize(), role);
            return users.map(userMapper::userEntityToUserDTO);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("No users found for role: {}", role, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching users with role {}: {}", role, e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching users.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByUsernameAndEmail(String username, String email, Pageable pageable) {
        logger.info("Fetching users by username: {} and email: {} with pagination: {}", username, email, pageable);

        if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            logger.error("Invalid input: username or email is empty");
            throw new CustomExceptions.BadRequestException("Username and email must be provided.");
        }

        try {
            Page<UserEntity> users = userRepository.findByUsernameAndEmailIgnoreCase(username, email, pageable);
            if (users.isEmpty()) {
                logger.warn("No users found with username: {} and email: {}", username, email);
                throw new CustomExceptions.NotFoundException("No users found with the provided username and email.");
            }

            logger.info("Successfully fetched {} users for username: {} and email: {}", users.getSize(), username, email);
            return users.map(userMapper::userEntityToUserDTO);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("No users found for username: {} and email: {}", username, email, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching users by username: {} and email: {}: {}", username, email, e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while fetching users.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> searchUser(String searchTerm) {
        logger.info("Searching users with search term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            logger.error("Search term is empty");
            throw new CustomExceptions.BadRequestException("Search term must be provided.");
        }

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
            Root<UserEntity> user = query.from(UserEntity.class);

            String lowerCaseSearchTerm = "%" + searchTerm.trim().toLowerCase() + "%";

            Predicate usernamePredicate = cb.like(cb.lower(user.get("username")), lowerCaseSearchTerm);
            Predicate emailPredicate = cb.like(cb.lower(user.get("email")), lowerCaseSearchTerm);

            query.where(cb.or(usernamePredicate, emailPredicate));

            List<UserEntity> users = entityManager.createQuery(query).getResultList();

            if (users.isEmpty()) {
                logger.error("No users found for search term: {}", searchTerm);
                throw new CustomExceptions.NotFoundException("No users found with the provided search term.");
            }

            logger.info("Successfully found {} users for search term: {}", users.size(), searchTerm);
            return users.stream()
                    .map(userMapper::userEntityToUserDTO)
                    .collect(Collectors.toList());

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("No users found for search term: {}", searchTerm, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while searching users with search term: {}. Error message: {}", searchTerm, e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while searching users.");
        }
    }

    @Override
    @Transactional
    public UserDTO modifyUser(Long id, UserDTO userDTO) {
        try {
            UserEntity existingUser = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new CustomExceptions.NotFoundException("User not found with ID: " + id);
                    });

            modifyUserDetails(existingUser, userDTO);

            if (userDTO.getUserProfile() == null) {
                logger.error("User profile is required for update, user ID: {}", id);
                throw new CustomExceptions.BadRequestException("User profile is required for update.");
            }

            modifyUserProfile(existingUser, userDTO);
            UserEntity updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with ID: {}", id);
            return userMapper.userEntityToUserDTO(updatedUser);

        } catch (CustomException e) {
            logger.error("Custom exception occurred while updating user with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred while updating user with ID: {}", id, e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while updating user.");
        }
    }

    private void modifyUserDetails(UserEntity existingUser, UserDTO userDTO) {
        boolean isUpdated = false;

        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty() && !userDTO.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(userDTO.getUsername());
            isUpdated = true;
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty() && !userDTO.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(userDTO.getEmail());
            isUpdated = true;
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            logger.info("Password hashed and updated for user ID: {}", existingUser.getId());
            isUpdated = true;
        }

        if (userDTO.getRole() != null && !userDTO.getRole().equals(existingUser.getRole())) {
            existingUser.setRole(userDTO.getRole());
            isUpdated = true;
        }

        if (isUpdated) {
            logger.info("User details updated for user ID: {}", existingUser.getId());
        }
    }

    private void modifyUserProfile(UserEntity existingUser, UserDTO userDTO) {
        if (existingUser.getUserProfile() == null) {
            UserProfileEntity newUserProfile = userProfileMapper.userProfileDTOToUserProfileEntity(userDTO.getUserProfile());
            newUserProfile.setUser(existingUser);
            existingUser.setUserProfile(newUserProfile);
            userProfileRepository.save(newUserProfile);
            logger.info("Created new user profile for user ID: {}", existingUser.getId());
        } else {
            UserProfileEntity userProfileEntity = existingUser.getUserProfile();
            boolean profileUpdated = false;

            if (userDTO.getUserProfile() != null) {
                if (userDTO.getUserProfile().getDateOfBirth() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    userProfileEntity.setDateOfBirth(LocalDate.parse(userDTO.getUserProfile().getDateOfBirth(), formatter));
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getGender() != null &&
                        !userDTO.getUserProfile().getGender().equals(userProfileEntity.getGender())) {
                    Gender genderEnum = Gender.fromString(userDTO.getUserProfile().getGender().toString());
                    userProfileEntity.setGender(genderEnum);
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getBiography() != null &&
                        !userDTO.getUserProfile().getBiography().equals(userProfileEntity.getBiography())) {
                    userProfileEntity.setBiography(userDTO.getUserProfile().getBiography());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getLocation() != null &&
                        !userDTO.getUserProfile().getLocation().equals(userProfileEntity.getLocation())) {
                    userProfileEntity.setLocation(userDTO.getUserProfile().getLocation());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getOccupation() != null &&
                        !userDTO.getUserProfile().getOccupation().equals(userProfileEntity.getOccupation())) {
                    userProfileEntity.setOccupation(userDTO.getUserProfile().getOccupation());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getEducation() != null &&
                        !userDTO.getUserProfile().getEducation().equals(userProfileEntity.getEducation())) {
                    userProfileEntity.setEducation(userDTO.getUserProfile().getEducation());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getInterests() != null &&
                        !userDTO.getUserProfile().getInterests().equals(userProfileEntity.getInterests())) {
                    userProfileEntity.setInterests(userDTO.getUserProfile().getInterests());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getProfileImageUrl() != null &&
                        !userDTO.getUserProfile().getProfileImageUrl().equals(userProfileEntity.getProfileImageUrl())) {
                    userProfileEntity.setProfileImageUrl(userDTO.getUserProfile().getProfileImageUrl());
                    profileUpdated = true;
                }
            }

            if (profileUpdated) {
                userProfileRepository.save(userProfileEntity);
                logger.info("User profile updated for user ID: {}", existingUser.getId());
            }
        }
    }

    @Override
    @Transactional
    public void removeUserById(Long id) {
        logger.info("Deleting user with ID: {}", id);

        try {
            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);

        } catch (EmptyResultDataAccessException e) {
            logger.error("User not found for delete with ID: {}", id, e);
            throw new CustomExceptions.NotFoundException("User not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with ID: {}", id, e);
            throw new CustomExceptions.InternalServerErrorException("An unexpected error occurred while deleting the user.");
        }
    }
}