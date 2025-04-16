package com.batubook.backend.Tests.UserTests;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.UserMapper;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceImplementation.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment...");
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        reset(userRepository, userMapper, passwordEncoder);
        logger.info("Test environment setup complete.");
    }

    @Test
    @Order(1)
    @DisplayName("Should create user with profile successfully")
    void shouldCreateUserWithProfileSuccessfully() {
        logger.info("Starting test for creating user with profile...");
        UserDTO userDTO = createSampleUserDTO();
        UserEntity userEntity = createSampleUserEntity();

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.userDTOToEntity(userDTO)).thenReturn(userEntity);
        when(userMapper.userEntityToDTO(userEntity)).thenReturn(userDTO);

        UserDTO result = userService.registerUser(userDTO);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(userDTO.getUsername(), result.getUsername()),
                () -> assertNotNull(result.getUserProfile())
        );

        verify(userRepository).save(any(UserEntity.class));
        verify(userMapper).userDTOToEntity(userDTO);
        verify(userMapper).userEntityToDTO(userEntity);
        logger.info("Test for creating user with profile completed.");
    }

    @Test
    @Order(2)
    @DisplayName("Should fail to create user when username is missing")
    void shouldFailToCreateUserWhenUsernameIsMissing() {
        logger.info("Starting test for failing to create user when username is missing...");
        UserDTO userDTO = createSampleUserDTO();
        userDTO.setUsername("");

        when(userMapper.userDTOToEntity(userDTO)).thenThrow(new CustomExceptions.InternalServerErrorException("Username cannot be empty"));
        CustomExceptions.InternalServerErrorException exception = assertThrows(CustomExceptions.InternalServerErrorException.class, () -> userService.registerUser(userDTO));
        assertEquals("User could not be created: Username cannot be empty", exception.getMessage());

        verify(userRepository, times(0)).save(any(UserEntity.class));
        logger.info("Test for failing to create user when username is missing completed.");
    }

    @Test
    @Order(3)
    @DisplayName("Should successfully retrieve user by ID")
    void shouldSuccessfullyRetrieveUserById() {
        logger.info("Starting test for retrieving user by ID...");
        Long userId = 1L;
        UserEntity userEntity = createSampleUserEntity();
        UserDTO expectedUserDTO = createSampleUserDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.userEntityToDTO(userEntity)).thenReturn(expectedUserDTO);

        UserDTO result = userService.getUserById(userId);
        assertNotNull(result);
        assertEquals(expectedUserDTO.getUsername(), result.getUsername());

        verify(userRepository).findById(userId);
        verify(userMapper).userEntityToDTO(userEntity);
        logger.info("Test for retrieving user by ID completed.");
    }

    @Test
    @Order(4)
    @DisplayName("Should throw exception when user is not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        logger.info("Starting test for retrieving user by ID (user not found)...");
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CustomExceptions.NotFoundException exception = assertThrows(CustomExceptions.NotFoundException.class, () -> userService.getUserById(userId));
        assertEquals("User not found with ID: 999", exception.getMessage());

        verify(userRepository).findById(userId);
        logger.info("Test for retrieving user by ID (user not found) completed.");
    }

    @Test
    @Order(5)
    @DisplayName("Should successfully retrieve all users with pagination")
    void shouldSuccessfullyRetrieveAllUsers() {
        logger.info("Starting test for retrieving all users with pagination...");
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(List.of(createSampleUserEntity()));
        Page<UserDTO> expectedUserDTOPage = userPage.map(userMapper::userEntityToDTO);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(userRepository).findAll(pageable);
        logger.info("Test for retrieving all users with pagination completed.");
    }

    @Test
    @Order(6)
    @DisplayName("Should successfully retrieve users by role")
    void shouldSuccessfullyRetrieveUsersByRole() {
        logger.info("Starting test for retrieving users by role...");
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(List.of(createSampleUserEntity()));
        Role role = Role.ADMIN;

        when(userRepository.findByRole(role, pageable)).thenReturn(userPage);
        Page<UserDTO> result = userService.getUsersByRole(role, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(userRepository).findByRole(role, pageable);
        logger.info("Test for retrieving users by role completed.");
    }

    @Test
    @Order(7)
    @DisplayName("Should successfully retrieve users by username and email")
    void shouldSuccessfullyRetrieveUsersByUsernameAndEmail() {
        logger.info("Starting test for retrieving users by username and email...");

        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(List.of(createSampleUserEntity()));
        Page<UserDTO> expectedUserDTOPage = userPage.map(userMapper::userEntityToDTO);
        String username = "bbatuhan";
        String email = "bbatuhan@batubook.com";

        when(userRepository.findByUsernameAndEmailIgnoreCase(username, email, pageable)).thenReturn(userPage);
        Page<UserDTO> result = userService.getUsersByUsernameAndEmail(username, email, pageable);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
        verify(userRepository).findByUsernameAndEmailIgnoreCase(username, email, pageable);
        logger.info("Test for retrieving users by username and email completed.");
    }

    @Test
    @Order(8)
    @DisplayName("Should successfully update user details")
    void shouldSuccessfullyUpdateUserDetails() {
        logger.info("Starting test for successfully updating user details...");
        Long userId = 1L;
        UserDTO userDTO = createSampleUserDTO();
        UserEntity existingUser = createSampleUserEntity();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.userDTOToEntity(userDTO)).thenReturn(existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.userEntityToDTO(existingUser)).thenReturn(userDTO);

        UserDTO result = userService.modifyUser(userId, userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getUserProfile().getBiography(), result.getUserProfile().getBiography());

        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
        verify(userMapper).userEntityToDTO(existingUser);
        logger.info("Test for successfully updating user details completed.");
    }

    @Test
    @Order(9)
    @DisplayName("Should fail to update user when user not found")
    void shouldFailToUpdateUserWhenUserNotFound() {
        logger.info("Starting test for failing to update user when user is not found...");
        Long userId = 999L;
        UserDTO userDTO = createSampleUserDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        CustomExceptions.NotFoundException exception = assertThrows(CustomExceptions.NotFoundException.class, () -> userService.modifyUser(userId, userDTO));
        assertEquals("User not found with ID: 999", exception.getMessage());

        verify(userRepository).findById(userId);
        logger.info("Test for failing to update user when user is not found completed.");
    }

    @Test
    @Order(10)
    @DisplayName("Should fail to update user with invalid email format")
    void shouldFailToUpdateUserWithInvalidEmailFormat() {
        logger.info("Starting test for failing to update user with invalid email format...");
        Long userId = 1L;
        UserDTO userDTO = createSampleUserDTO();
        userDTO.setEmail("invalidEmail");

        UserEntity existingUser = createSampleUserEntity();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.userDTOToEntity(userDTO))
                .thenThrow(new CustomExceptions.InternalServerErrorException("Invalid email format"));

        CustomExceptions.InternalServerErrorException exception =
                assertThrows(CustomExceptions.InternalServerErrorException.class,
                        () -> userService.modifyUser(userId, userDTO));

        assertEquals("User could not be updated: Invalid email format", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, times(0)).save(any(UserEntity.class));
        verify(userMapper).userDTOToEntity(userDTO);
        logger.info("Test for failing to update user with invalid email format completed.");
    }

    @Test
    @Order(11)
    @DisplayName("Should fail to update user when username is empty")
    void shouldFailToUpdateUserWhenUsernameIsEmpty() {
        logger.info("Starting test for failing to update user when username is empty...");
        Long userId = 1L;
        UserDTO userDTO = createSampleUserDTO();
        userDTO.setUsername("");

        UserEntity existingUser = createSampleUserEntity();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.userDTOToEntity(userDTO))
                .thenThrow(new CustomExceptions.InternalServerErrorException("Username cannot be null or empty."));

        CustomExceptions.InternalServerErrorException exception =
                assertThrows(CustomExceptions.InternalServerErrorException.class,
                        () -> userService.modifyUser(userId, userDTO));

        assertEquals("User could not be updated: Username cannot be null or empty.", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userMapper).userDTOToEntity(userDTO);
        verify(userRepository, times(0)).save(any(UserEntity.class));
        logger.info("Test for failing to update user when username is empty completed.");
    }

    @Test
    @Order(12)
    @DisplayName("Should fail to update user when role is invalid")
    void shouldFailToUpdateUserWhenRoleIsInvalid() {
        logger.info("Starting test for failing to update user with invalid role...");
        Long userId = 1L;
        UserDTO userDTO = createSampleUserDTO();
        userDTO.setRole(null);

        UserEntity existingUser = createSampleUserEntity();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.userDTOToEntity(userDTO))
                .thenThrow(new CustomExceptions.InternalServerErrorException("Invalid role."));

        CustomExceptions.InternalServerErrorException exception =
                assertThrows(CustomExceptions.InternalServerErrorException.class,
                        () -> userService.modifyUser(userId, userDTO));

        assertEquals("User could not be updated: Invalid role.", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userMapper).userDTOToEntity(userDTO);
        verify(userRepository, times(0)).save(any(UserEntity.class));
        logger.info("Test for failing to update user with invalid role completed.");
    }

    @Test
    @Order(13)
    @DisplayName("Should successfully remove user by ID")
    void shouldSuccessfullyRemoveUserById() {
        logger.info("Starting test for successfully removing user by ID...");
        Long userId = 1L;
        UserEntity userEntity = createSampleUserEntity();

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);
        userService.removeUser(userId);

        verify(userRepository).deleteById(userId);
        verify(userRepository).existsById(userId);
        logger.info("Test for successfully removing user by ID completed.");
    }

    @Test
    @Order(14)
    @DisplayName("Should throw exception when user is not found by ID")
    void shouldThrowExceptionWhenUserNotFoundByIdForRemove() {
        logger.info("Starting test for failing to remove user when user not found...");
        Long userId = 999L;

        when(userRepository.existsById(userId)).thenReturn(false);
        CustomExceptions.NotFoundException exception = assertThrows(
                CustomExceptions.NotFoundException.class,
                () -> userService.removeUser(userId)
        );

        assertEquals("User not found with ID: 999", exception.getMessage());
        verify(userRepository, times(0)).deleteById(userId);
        verify(userRepository).existsById(userId);
        logger.info("Test for failing to remove user when user not found completed.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up after test...");
        reset(userRepository, userMapper, passwordEncoder);
        logger.info("Cleanup complete.");
    }

    private UserEntity createSampleUserEntity() {
        UserProfileEntity userProfile = createSampleUserProfileEntity(null);
        return UserEntity.builder()
                .id(1L)
                .username("bbatuhan")
                .email("bbatuhan@batubook.com")
                .password("validPassword!123")
                .role(Role.ADMIN)
                .userProfile(userProfile)
                .build();
    }

    private UserProfileEntity createSampleUserProfileEntity(UserEntity user) {
        return UserProfileEntity.builder()
                .user(user)
                .biography("Full Stack Software Developer")
                .location("Istanbul")
                .dateOfBirth(LocalDate.of(2000, 8, 14))
                .gender(Gender.MALE)
                .profileImageUrl("https://batubook.com/profile.jpg")
                .education("Computer Engineering")
                .occupation("Software Developer")
                .interests("Coding, Reading, Music")
                .build();
    }

    private UserDTO createSampleUserDTO() {
        UserProfileDTO userProfileDTO = createSampleUserProfileDTO();
        return UserDTO.builder()
                .id(1L)
                .username("bbatuhan")
                .email("bbatuhan@batubook.com")
                .password("validPassword!123")
                .role(Role.ADMIN)
                .userProfile(userProfileDTO)
                .build();
    }

    private UserProfileDTO createSampleUserProfileDTO() {
        return UserProfileDTO.builder()
                .biography("Full Stack Software Developer")
                .location("Istanbul")
                .dateOfBirth(String.valueOf(LocalDate.of(2000, 8, 14)))
                .gender(Gender.MALE)
                .profileImageUrl("https://batubook.com/profile.jpg")
                .education("Computer Engineering")
                .occupation("Software Developer")
                .interests("Coding, Reading, Music")
                .build();
    }
}