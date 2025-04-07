package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.UserMapper;
import com.batubook.backend.mapper.UserProfileMapper;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.UserServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        logger.info("Starting user creation process for username: {}", userDTO.getUsername());

        try {
            validateUserDTO(userDTO);
            UserEntity userEntity = userMapper.userDTOToEntity(userDTO);
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            UserProfileEntity userProfileEntity = mapUserProfile(userDTO);
            userProfileEntity.setUser(userEntity);
            userEntity.setUserProfile(userProfileEntity);

            UserEntity savedUser = userRepository.save(userEntity);
            logger.info("User created successfully: {}", userDTO.getUsername());
            return userMapper.userEntityToDTO(savedUser);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating user: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("User could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        logger.info("Attempting to retrieve user with ID: {}", id);
        UserEntity userById = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("User not found with ID: " + id);
                });

        logger.info("Successfully retrieved user with ID: {}", id);
        return userMapper.userEntityToDTO(userById);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserEntity> allUsers = userRepository.findAll(pageable);
        logger.info("Successfully fetched {} users", allUsers.getNumberOfElements());
        return allUsers.map(userMapper::userEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByRole(Role role, Pageable pageable) {
        logger.info("Fetching users with role: {} and pagination: {}", role, pageable);
        Page<UserEntity> users = userRepository.findByRole(role, pageable);
        logger.info("Successfully fetched {} users with role {}", users.getNumberOfElements(), role);
        return users.map(userMapper::userEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByUsernameAndEmail(String username, String email, Pageable pageable) {
        logger.info("Fetching users by username: {} and email: {} with pagination: {}", username, email, pageable);
        Page<UserEntity> users = userRepository.findByUsernameAndEmailIgnoreCase(username, email, pageable);
        logger.info("Successfully fetched {} users for username: {} and email: {}", users.getNumberOfElements(), username, email);
        return users.map(userMapper::userEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getUserByCriteria(String searchTerm, Pageable pageable) {
        logger.info("Searching users with search term: '{}' using pagination: page number = {}, page size = {}",
                searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
            Root<UserEntity> user = query.from(UserEntity.class);

            Predicate usernamePredicate = cb.like(cb.lower(user.get("username")), "%" + searchTerm.toLowerCase() + "%");
            Predicate emailPredicate = cb.like(cb.lower(user.get("email")), "%" + searchTerm.toLowerCase() + "%");
            query.where(cb.or(usernamePredicate, emailPredicate));

            TypedQuery<UserEntity> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<UserEntity> users = typedQuery.getResultList();
            logger.info("Found {} users for search term '{}'", users.size(), searchTerm);
            return new PageImpl<>(users.stream()
                    .map(userMapper::userEntityToDTO)
                    .collect(Collectors.toList()), pageable, users.size());

        } catch (Exception e) {
            logger.error("Error occurred while searching for users with search term '{}'", searchTerm, e);
            throw new RuntimeException("Error occurred while searching for users", e);
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
            return userMapper.userEntityToDTO(updatedUser);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating user: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("User could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        logger.info("Attempting to remove user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            logger.error("User with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        logger.info("Successfully deleted user with ID: {}", id);
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
        return userProfileMapper.userProfileDTOToEntity(userDTO.getUserProfile());
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
            UserProfileEntity newUserProfile = userProfileMapper.userProfileDTOToEntity(userDTO.getUserProfile());
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
}