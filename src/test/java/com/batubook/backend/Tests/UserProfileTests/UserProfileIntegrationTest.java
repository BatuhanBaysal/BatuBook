package com.batubook.backend.Tests.UserProfileTests;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.service.serviceImplementation.UserProfileServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserProfileServiceImpl userProfileService;

    private UserProfileDTO mockUserProfileDTO;

    private UserProfileEntity mock;

    @BeforeEach
    void setup() {
        userProfileRepository.deleteAll();
        initializeMockData();
    }

    @Test
    @Order(1)
    @DisplayName("Test User Profile Retrieval")
    void getUserProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user-profiles/{id}", mockUserProfileDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.biography").value("Test Biography"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Test Location"));

        logger.info("User profile retrieved successfully.");
    }

    @Test
    @Order(2)
    @DisplayName("Test User Profile Update")
    void updateUserProfile_ShouldReturnUpdatedProfile() throws Exception {
        mockUserProfileDTO.setBiography("Updated Biography");
        mockUserProfileDTO.setLocation("Updated Location");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user-profiles/update/{id}", mockUserProfileDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUserProfileDTO)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.biography").value("Updated Biography"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Updated Location"));

        logger.info("User profile updated successfully.");
    }

    @Test
    @Order(3)
    @DisplayName("Test User Profile Deletion")
    void deleteUserProfile_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user-profiles/delete/{id}", mockUserProfileDTO.getId()))
                .andExpect(status().isNoContent());

        logger.info("User profile deleted successfully.");
    }

    @Test
    @Order(4)
    @DisplayName("Test User Profile Not Found")
    void getUserProfile_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user-profiles/{id}", 99999))
                .andExpect(status().isNotFound());

        logger.info("User profile not found for invalid ID.");
    }

    @Test
    @Order(5)
    @DisplayName("It should return 400 Bad Request when updating with empty location (Service-level validation)")
    void updateUserProfile_ShouldReturnBadRequest_WhenLocationIsInvalid() throws Exception {
        // Arrange
        mockUserProfileDTO.setLocation("");

        // Act & Assert
        mockMvc.perform(
                        put("/api/user-profiles/update/{id}", mockUserProfileDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserProfileDTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Location cannot be empty")));

        logger.info("Invalid update correctly resulted in 400 due to service-side validation failure.");
    }

    private void initializeMockData() {
        UserEntity mockUserEntity = createMockUserEntity();
        UserProfileEntity mockUserProfileEntity = createMockUserProfileEntity(mockUserEntity);
        mockUserProfileEntity = userProfileRepository.save(mockUserProfileEntity);

        mockUserProfileDTO = convertEntityToDTO(mockUserProfileEntity);
    }

    private UserEntity createMockUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("TestUser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("Valid_Password!123");
        return userEntity;
    }

    private UserProfileEntity createMockUserProfileEntity(UserEntity userEntity) {
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setBiography("Test Biography");
        userProfileEntity.setDateOfBirth(LocalDate.of(2000, 8, 14));
        userProfileEntity.setGender(Gender.MALE);
        userProfileEntity.setLocation("Test Location");
        userProfileEntity.setProfileImageUrl("mock/image/path/test.jpg");
        userProfileEntity.setUser(userEntity);
        return userProfileEntity;
    }

    private UserProfileDTO convertEntityToDTO(UserProfileEntity userProfileEntity) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(userProfileEntity.getId());
        userProfileDTO.setBiography(userProfileEntity.getBiography());
        userProfileDTO.setDateOfBirth(String.valueOf(userProfileEntity.getDateOfBirth()));
        userProfileDTO.setGender(userProfileEntity.getGender());
        userProfileDTO.setLocation(userProfileEntity.getLocation());
        userProfileDTO.setProfileImageUrl(userProfileEntity.getProfileImageUrl());
        return userProfileDTO;
    }
}