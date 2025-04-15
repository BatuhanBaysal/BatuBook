package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.UserMapper;
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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        try {
            logger.info("Starting user creation process for username: {}", userDTO.getUsername());
            UserEntity userEntity = userMapper.userDTOToEntity(userDTO);
            logger.debug("Validating user entity for username: {}", userDTO.getUsername());
            validateUserEntity(userEntity);
            UserEntity savedUser = userRepository.save(userEntity);
            logger.info("User created successfully: {}", userDTO.getUsername());
            return userMapper.userEntityToDTO(savedUser);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error while creating user with username {}: {}", userDTO.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating user with username {}: {}", userDTO.getUsername(), e.getMessage());
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
        try {
            logger.info("Searching users with search term: '{}' using pagination: page number = {}, page size = {}",
                    searchTerm, pageable.getPageNumber(), pageable.getPageSize());
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
            logger.info("Attempting to find user with ID: {}", id);
            UserEntity existingUser = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new CustomExceptions.NotFoundException("User not found with ID: " + id);
                    });

            logger.debug("Modifying user details for user ID: {}", id);
            UserEntity userEntity = userMapper.userDTOToEntity(userDTO);
            modifyUserDetails(existingUser, userEntity);

            UserEntity updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with ID: {}", id);
            return userMapper.userEntityToDTO(updatedUser);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("User not found with ID: {}. Error message: {}", id, e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("Error while updating user with ID: {}. Error message: {}", id, e.getMessage());
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

    private void validateAndSetField(String field, String value, Consumer<String> setter, String errorMessage, boolean isPassword, boolean isRequired) {
        if (isRequired && (value == null || value.trim().isEmpty())) {
            logger.error("{} cannot be null or empty.", field);
            throw new CustomExceptions.BadRequestException(errorMessage);
        }

        if (value != null && !value.trim().isEmpty()) {
            if (isPassword) {
                setter.accept(passwordEncoder.encode(value.trim()));
                logger.info("{} create/updated (password changed).", field);
            } else {
                setter.accept(value);
                logger.info("{} create/updated: {}", field, value);
            }
        } else {
            if (isPassword) {
                logger.error("{} cannot be null or empty.", field);
                throw new CustomExceptions.BadRequestException(errorMessage);
            }
        }
    }

    private void validateUserEntity(UserEntity userEntity) {
        validateAndSetField("Username", userEntity.getUsername(), userEntity::setUsername, "Username cannot be empty.", false, true);
        validateAndSetField("Email", userEntity.getEmail(), userEntity::setEmail, "Email cannot be empty.", false, true);
        validateAndSetField("Password", userEntity.getPassword(), userEntity::setPassword, "Password cannot be empty.", true, true);

        if (userEntity.getRole() == null) {
            logger.error("Role is empty for username: {}", userEntity.getUsername());
            throw new CustomExceptions.BadRequestException("Role cannot be empty.");
        } else {
            logger.info("Role validated for username: {}", userEntity.getUsername());
        }

        if (userEntity.getUserProfile() == null) {
            logger.error("User profile is missing for username: {}", userEntity.getUsername());
            throw new CustomExceptions.BadRequestException("User profile is required.");
        } else {
            logger.info("User profile validated for username: {}", userEntity.getUsername());

            UserProfileEntity userProfileEntity = userEntity.getUserProfile();
            userProfileEntity.setUser(userEntity);

            try {
                validateAndSetField("Date of Birth", String.valueOf(userProfileEntity.getDateOfBirth()),
                        value -> userProfileEntity.setDateOfBirth(LocalDate.parse(value)), "Date of Birth cannot be null or empty.", false, true);
            } catch (DateTimeParseException e) {
                logger.error("Invalid date format for username: {}", userEntity.getUsername());
                throw new CustomExceptions.BadRequestException("Invalid date format.");
            }

            validateAndSetField("Biography", userProfileEntity.getBiography(),
                    userProfileEntity::setBiography, "Biography cannot be null or empty.", false, true);

            validateAndSetField("Location", userProfileEntity.getLocation(),
                    userProfileEntity::setLocation, "Location cannot be null or empty.", false, true);

            validateAndSetField("Occupation", userProfileEntity.getOccupation(),
                    userProfileEntity::setOccupation, "Occupation cannot be null or empty.", false, false);

            validateAndSetField("Education", userProfileEntity.getEducation(),
                    userProfileEntity::setEducation, "Education cannot be null or empty.", false, false);

            validateAndSetField("Interests", userProfileEntity.getInterests(),
                    userProfileEntity::setInterests, "Interests cannot be null or empty.", false, false);

            validateAndSetField("Profile Image URL", userProfileEntity.getProfileImageUrl(),
                    userProfileEntity::setProfileImageUrl, "Profile Image URL cannot be null or empty.", false, false);
        }
    }

    private void modifyUserDetails(UserEntity existingUser, UserEntity userEntity) {
        validateAndSetField("Username", userEntity.getUsername(), existingUser::setUsername, "Username cannot be null or empty.", false,true);
        validateAndSetField("Email", userEntity.getEmail(), existingUser::setEmail, "Email cannot be null or empty.", false,true);
        validateAndSetField("Password", userEntity.getPassword(), existingUser::setPassword, "Password cannot be null or empty.", true,true);

        if (userEntity.getRole() != null) {
            Role roleEnum = Role.fromString(userEntity.getRole().toString());
            if (roleEnum == null) {
                logger.error("Invalid role for user Id: {}", userEntity.getId());
                throw new CustomExceptions.BadRequestException("Invalid role.");
            }
            existingUser.setRole(roleEnum);
            logger.info("Role updated for user ID: {}. New Role: {}", existingUser.getId(), roleEnum);
        }

        modifyUserProfile(existingUser, userEntity);
    }

    private void modifyUserProfile(UserEntity existingUser, UserEntity userEntity) {
        if (existingUser.getUserProfile() == null) {
            logger.error("User profile is missing for modifyUserProfile, user Id: {}", existingUser.getId());
            throw new CustomExceptions.BadRequestException("User profile is required for update.");
        }

        UserProfileEntity userProfileEntity = existingUser.getUserProfile();

        try {
            validateAndSetField("Date of Birth", String.valueOf(userEntity.getUserProfile().getDateOfBirth()),
                    value -> userProfileEntity.setDateOfBirth(LocalDate.parse(value)), "Date of Birth cannot be null or empty.", false,true);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format for user ID: {}", existingUser.getId());
            throw new CustomExceptions.BadRequestException("Invalid date format.");
        }

        validateAndSetField("Biography", userEntity.getUserProfile().getBiography(),
                userProfileEntity::setBiography, "Biography cannot be null or empty.", false,true);

        validateAndSetField("Location", userEntity.getUserProfile().getLocation(),
                userProfileEntity::setLocation, "Location cannot be null or empty.", false,true);

        validateAndSetField("Occupation", userEntity.getUserProfile().getOccupation(),
                userProfileEntity::setOccupation, "Occupation cannot be null or empty.", false, false);

        validateAndSetField("Education", userEntity.getUserProfile().getEducation(),
                userProfileEntity::setEducation, "Education cannot be null or empty.", false, false);

        validateAndSetField("Interests", userEntity.getUserProfile().getInterests(),
                userProfileEntity::setInterests, "Interests cannot be null or empty.", false, false);

        validateAndSetField("Profile Image URL", userEntity.getUserProfile().getProfileImageUrl(),
                userProfileEntity::setProfileImageUrl, "Profile Image URL cannot be null or empty.", false, false);
    }
}