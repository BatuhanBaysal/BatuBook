package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.mapper.UserMapper;
import com.batubook.backend.mapper.UserProfileMapper;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.UserServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public UserDTO createUser(UserDTO userDTO) {
        try {
            if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
                logger.error("Password is empty for user: {}", userDTO.getUsername());
                throw new IllegalArgumentException("Password cannot be empty.");
            }

            UserEntity userEntity = userMapper.userDTOToUserEntity(userDTO);
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            if (userDTO.getUserProfile() == null) {
                logger.error("User profile is missing for user: {}", userDTO.getUsername());
                throw new IllegalArgumentException("User profile is required.");
            }

            UserProfileEntity userProfileEntity = userProfileMapper.userProfileDTOToUserProfileEntity(userDTO.getUserProfile());
            userProfileEntity.setUser(userEntity);
            userEntity.setUserProfile(userProfileEntity);

            UserEntity savedUser = userRepository.save(userEntity);
            logger.info("User created successfully: {}", userDTO.getUsername());
            return userMapper.userEntityToUserDTO(savedUser);
        } catch (Exception e) {
            logger.error("Error occurred while creating user: {}", userDTO.getUsername(), e);
            throw e;
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        try {
            UserEntity userEntity = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new EntityNotFoundException("User not found with ID: " + id);
                    });

            logger.info("User retrieved successfully with ID: {}", id);
            return userMapper.userEntityToUserDTO(userEntity);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving user with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            logger.info("Successfully fetched {} users.", users.size());
            return users.stream().map(userMapper::userEntityToUserDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<UserDTO> getUsersByRole(Role role) {
        try {
            List<UserEntity> userRoles = userRepository.findByRole(role);
            logger.info("Successfully fetched {} users with role {}.", userRoles.size(), role);
            return userRoles.stream().map(userMapper::userEntityToUserDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching users by role {}: {}", role, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<UserDTO> getUsersByUsernameAndEmail(String username, String email) {
        try {
            List<UserEntity> users = userRepository.findByUsernameAndEmailIgnoreCase(username, email);
            logger.info("Successfully fetched {} users for username: {} and email: {}.", users.size(), username, email);
            return users.stream().map(userMapper::userEntityToUserDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching users by username {} and email {}: {}", username, email, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<UserDTO> searchUser(String searchTerm) {
        try {
            logger.info("Searching users with term: {}", searchTerm);
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
            Root<UserEntity> user = query.from(UserEntity.class);

            Predicate usernamePredicate = cb.like(cb.lower(user.get("username")), "%" + searchTerm.toLowerCase() + "%");
            Predicate emailPredicate = cb.like(cb.lower(user.get("email")), "%" + searchTerm.toLowerCase() + "%");

            query.where(cb.or(usernamePredicate, emailPredicate));
            List<UserEntity> users = entityManager.createQuery(query).getResultList();
            logger.info("Successfully found {} users for search term: {}", users.size(), searchTerm);
            return users.stream().map(userMapper::userEntityToUserDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching users with term {}: {}", searchTerm, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        try {
            UserEntity existingUser = userRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", id);
                        return new EntityNotFoundException("User not found with ID: " + id);
                    });

            updateUserDetails(existingUser, userDTO);
            if (userDTO.getUserProfile() == null) {
                logger.error("User profile is required for update, user ID: {}", id);
                throw new IllegalArgumentException("User profile is required for update.");
            }

            updateUserProfile(existingUser, userDTO);
            UserEntity updatedUser = userRepository.save(existingUser);
            logger.info("User updated successfully with ID: {}", id);
            return userMapper.userEntityToUserDTO(updatedUser);
        } catch (Exception e) {
            logger.error("Error occurred while updating user with ID: {}", id, e);
            throw e;
        }
    }

    private void updateUserDetails(UserEntity existingUser, UserDTO userDTO) {
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setRole(userDTO.getRole() != null ?
                Role.valueOf(userDTO.getRole().toUpperCase()) : existingUser.getRole());
    }

    private void updateUserProfile(UserEntity existingUser, UserDTO userDTO) {
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
                    userProfileEntity.setDateOfBirth(LocalDate.parse(userDTO.getUserProfile().getDateOfBirth()));
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getGender() != null) {
                    userProfileEntity.setGender(userDTO.getUserProfile().getGender());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getBiography() != null) {
                    userProfileEntity.setBiography(userDTO.getUserProfile().getBiography());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getLocation() != null) {
                    userProfileEntity.setLocation(userDTO.getUserProfile().getLocation());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getOccupation() != null) {
                    userProfileEntity.setOccupation(userDTO.getUserProfile().getOccupation());
                    profileUpdated = true;
                }
                if (userDTO.getUserProfile().getEducation() != null) {
                    userProfileEntity.setEducation(userDTO.getUserProfile().getEducation());
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
    public void deleteUserById(Long id) {
        try {
            if (!userRepository.existsById(id)) {
                logger.error("User not found with ID: {}", id);
                throw new EntityNotFoundException("User not found with ID: " + id);
            }

            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error occurred while deleting user with ID: {}", id, e);
            throw e;
        }
    }
}