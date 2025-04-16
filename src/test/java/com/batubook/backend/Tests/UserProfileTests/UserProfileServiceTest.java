package com.batubook.backend.Tests.UserProfileTests;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.UserProfileMapper;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.service.serviceImplementation.UserProfileServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserProfileServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceTest.class);

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfileEntity userProfileEntity;
    private UserProfileDTO userProfileDTO;
    private final Long userProfileId = 1L;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        logger.info("Setting up mock data for tests...");
        pageable = PageRequest.of(0, 10);
        initializeTestData();
    }

    @Test
    @Order(1)
    @DisplayName("Should return user profile by ID - Success")
    void getUserProfileById_Success() {
        when(userProfileRepository.findById(userProfileId)).thenReturn(Optional.of(userProfileEntity));
        when(userProfileMapper.userProfileEntityToDTO(userProfileEntity)).thenReturn(userProfileDTO);

        UserProfileDTO result = userProfileService.getUserProfileById(userProfileId);

        assertNotNull(result);
        assertEquals(userProfileId, result.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Should throw NotFoundException when user profile ID not found")
    void getUserProfileById_NotFound() {
        when(userProfileRepository.findById(userProfileId)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.NotFoundException.class,
                () -> userProfileService.getUserProfileById(userProfileId));

        Exception exception = assertThrows(CustomExceptions.NotFoundException.class,
                () -> userProfileService.getUserProfileById(userProfileId));
        assertEquals("User profile not found with ID: " + userProfileId, exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Should return all user profiles - Success")
    void getAllUserProfiles_Success() {
        Page<UserProfileEntity> page = new PageImpl<>(List.of(userProfileEntity));
        when(userProfileRepository.findAll(pageable)).thenReturn(page);
        when(userProfileMapper.userProfileEntityToDTO(userProfileEntity)).thenReturn(userProfileDTO);

        Page<UserProfileDTO> result = userProfileService.getAllUserProfiles(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @Order(4)
    @DisplayName("Should return empty when no user profiles exist")
    void getAllUserProfiles_EmptyResult() {
        Page<UserProfileEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userProfileRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserProfileDTO> result = userProfileService.getAllUserProfiles(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Should return user profiles by date of birth - Success")
    void getUserProfilesByBirthDate_Success() {
        LocalDate dob = LocalDate.of(2000, 8, 14);
        Page<UserProfileEntity> page = new PageImpl<>(List.of(userProfileEntity));
        when(userProfileRepository.findByDateOfBirth(dob, pageable)).thenReturn(page);
        when(userProfileMapper.userProfileEntityToDTO(userProfileEntity)).thenReturn(userProfileDTO);

        Page<UserProfileDTO> result = userProfileService.getUserProfilesByBirthDate(dob, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @Order(6)
    @DisplayName("Should return empty when date of birth does not match")
    void getUserProfilesByBirthDate_NotFound() {
        LocalDate dob = LocalDate.of(1980, 1, 1);
        when(userProfileRepository.findByDateOfBirth(dob, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<UserProfileDTO> result = userProfileService.getUserProfilesByBirthDate(dob, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Should return user profiles by gender - Success")
    void getUserProfilesByGender_Success() {
        Page<UserProfileEntity> page = new PageImpl<>(List.of(userProfileEntity));
        when(userProfileRepository.findByGender(Gender.MALE, pageable)).thenReturn(page);
        when(userProfileMapper.userProfileEntityToDTO(userProfileEntity)).thenReturn(userProfileDTO);

        Page<UserProfileDTO> result = userProfileService.getUserProfilesByGender(Gender.MALE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @Order(8)
    @DisplayName("Should return empty when gender does not match")
    void getUserProfilesByGender_NotFound() {
        when(userProfileRepository.findByGender(Gender.FEMALE, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<UserProfileDTO> result = userProfileService.getUserProfilesByGender(Gender.FEMALE, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("Should update user profile successfully")
    void modifyUserProfile_Success() {
        when(userProfileRepository.findById(userProfileId)).thenReturn(Optional.of(userProfileEntity));
        when(userProfileRepository.save(Mockito.any(UserProfileEntity.class))).thenReturn(userProfileEntity);
        when(userProfileMapper.userProfileEntityToDTO(userProfileEntity)).thenReturn(userProfileDTO);

        UserProfileDTO result = userProfileService.modifyUserProfile(userProfileId, userProfileDTO);

        assertNotNull(result);
        assertEquals(userProfileId, result.getId());
    }

    @Test
    @Order(10)
    @DisplayName("Should throw NotFoundException when modifying non-existent profile")
    void modifyUserProfile_NotFound() {
        when(userProfileRepository.findById(userProfileId)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.NotFoundException.class,
                () -> userProfileService.modifyUserProfile(userProfileId, userProfileDTO));
    }

    @Test
    @Order(11)
    @DisplayName("Should throw InternalServerErrorException on DB error during update")
    void modifyUserProfile_DatabaseError() {
        when(userProfileRepository.findById(userProfileId)).thenReturn(Optional.of(userProfileEntity));
        when(userProfileRepository.save(Mockito.any(UserProfileEntity.class)))
                .thenThrow(new RuntimeException("DB failure"));

        assertThrows(CustomExceptions.InternalServerErrorException.class,
                () -> userProfileService.modifyUserProfile(userProfileId, userProfileDTO));
    }

    @Test
    @Order(12)
    @DisplayName("Should delete user profile successfully")
    void removeUserProfile_Success() {
        when(userProfileRepository.existsById(userProfileId)).thenReturn(true);
        doNothing().when(userProfileRepository).deleteById(userProfileId);

        assertDoesNotThrow(() -> userProfileService.removeUserProfile(userProfileId));
        verify(userProfileRepository, times(1)).deleteById(userProfileId);
    }

    @Test
    @Order(13)
    @DisplayName("Should throw NotFoundException when deleting non-existent profile")
    void removeUserProfile_NotFound() {
        when(userProfileRepository.existsById(userProfileId)).thenReturn(false);

        assertThrows(CustomExceptions.NotFoundException.class,
                () -> userProfileService.removeUserProfile(userProfileId));
    }

    private void initializeTestData() {
        userProfileEntity = UserProfileEntity.builder()
                .id(userProfileId)
                .dateOfBirth(LocalDate.of(2000, 8, 14))
                .gender(Gender.MALE)
                .biography("Test bio")
                .location("Istanbul")
                .occupation("Engineer")
                .education("University")
                .interests("Coding")
                .user(new UserEntity())
                .build();

        userProfileDTO = UserProfileDTO.builder()
                .id(userProfileId)
                .dateOfBirth(String.valueOf(LocalDate.of(2000, 8, 14)))
                .gender(Gender.MALE)
                .biography("Test bio")
                .location("Istanbul")
                .occupation("Engineer")
                .education("University")
                .interests("Coding")
                .build();
    }
}