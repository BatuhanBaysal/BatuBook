package com.batubook.backend.Tests.UserProfileTests;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.repository.UserProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserProfileControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UserProfileDTO mockUserProfileDTO;
    private UserProfileEntity mockUserProfileEntity;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all UserProfileController tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for UserProfileController tests...");
        userProfileRepository.deleteAll();
        initializeMockData();
        logger.info("Test data initialized successfully.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Test completed, no database changes should remain due to @Transactional.");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("All UserProfileController tests completed.");
    }

    @Test
    @Order(1)
    @DisplayName("It should fetch a user profile by ID successfully")
    void fetchUserProfileById_success() throws Exception {
        logger.info("Starting test: It should fetch a user profile by ID successfully.");

        mockMvc.perform(get("/api/user-profiles/{id}", mockUserProfileEntity.getId()))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(mockUserProfileEntity.getId()),
                        jsonPath("$.biography").value("Test Biography"),
                        jsonPath("$.dateOfBirth").value("2000-08-14"),
                        jsonPath("$.gender").value("male"),
                        jsonPath("$.location").value("Test Location")
                );

        logger.info("Test passed: User profile fetched successfully by ID: {}", mockUserProfileEntity.getId());
    }

    @Test
    @Order(2)
    @DisplayName("It should return 404 when user profile not found by ID")
    void fetchUserProfileById_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when user profile not found by ID.");

        mockMvc.perform(get("/api/user-profiles/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Test passed: No user profile found with the provided ID.");
    }

    @Test
    @Order(3)
    @DisplayName("It should return all user profiles with pagination and sorting")
    void fetchAllUserProfiles_success() throws Exception {
        logger.info("Starting test: It should return all user profiles with pagination and sorting.");

        mockMvc.perform(get("/api/user-profiles?page=0&size=5&sort=id,asc"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content.length()").value(1),
                        jsonPath("$.content[0].id").value(mockUserProfileEntity.getId()),
                        jsonPath("$.content[0].biography").value("Test Biography"),
                        jsonPath("$.content[0].dateOfBirth").value("2000-08-14"),
                        jsonPath("$.content[0].gender").value("male"),
                        jsonPath("$.content[0].location").value("Test Location"),
                        jsonPath("$.totalElements").value(1),
                        jsonPath("$.totalPages").value(1),
                        jsonPath("$.last").value(true),
                        jsonPath("$.pageable").exists()
                );

        logger.info("Test passed: User profiles returned with pagination and sorting.");
    }

    @Test
    @Order(4)
    @DisplayName("It should return empty content when no user profiles exist")
    void fetchAllUserProfiles_empty() throws Exception {
        logger.info("Starting test: It should return empty content when no user profiles exist.");

        userProfileRepository.deleteAll();

        mockMvc.perform(get("/api/user-profiles?page=0&size=5&sort=id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        logger.info("Test passed: No user profiles found, content is empty.");
    }

    @Test
    @Order(5)
    @DisplayName("It should fetch user profiles by date of birth")
    void fetchUserProfilesByBirthday_success() throws Exception {
        logger.info("Starting test: It should fetch user profiles by date of birth.");

        mockMvc.perform(get("/api/user-profiles/search-birthday")
                        .param("dateOfBirth", "2000-08-14"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content[0].id").value(mockUserProfileEntity.getId()),
                        jsonPath("$.content[0].dateOfBirth").value("2000-08-14")
                );

        logger.info("Test passed: User profiles fetched by date of birth.");
    }

    @Test
    @Order(6)
    @DisplayName("It should return an empty page when no user profiles found by date of birth")
    void fetchUserProfilesByBirthday_emptyResult() throws Exception {
        logger.info("Starting test: It should return an empty page when no user profiles found by date of birth.");

        mockMvc.perform(get("/api/user-profiles/search-birthday")
                        .param("dateOfBirth", "1999-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));

        logger.info("Test passed: No user profiles found for the provided date of birth.");
    }

    @Test
    @Order(7)
    @DisplayName("It should fetch user profiles by gender")
    void fetchUserProfilesByGender_success() throws Exception {
        logger.info("Starting test: It should fetch user profiles by gender.");

        mockMvc.perform(get("/api/user-profiles/search-gender")
                        .param("gender", "MALE"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content[0].id").value(mockUserProfileEntity.getId()),
                        jsonPath("$.content[0].gender").value("male")
                );

        logger.info("Test passed: User profiles fetched by gender.");
    }

    @Test
    @Order(8)
    @DisplayName("It should return 400 for invalid gender parameter")
    void fetchUserProfilesByGender_notFound() throws Exception {
        logger.info("Starting test: It should return 400 for invalid gender parameter.");

        mockMvc.perform(get("/api/user-profiles/search-gender")
                        .param("gender", "NON_EXISTENT"))
                .andExpect(status().isBadRequest());

        logger.info("Test passed: Invalid gender parameter resulted in 400.");
    }

    @Test
    @Order(9)
    @DisplayName("It should update user profile successfully")
    void updateUserProfile_success() throws Exception {
        logger.info("Starting test: It should update user profile successfully.");

        mockUserProfileDTO.setBiography("Updated Biography");

        mockMvc.perform(put("/api/user-profiles/update/{id}", mockUserProfileEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockUserProfileDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biography").value("Updated Biography"));

        logger.info("Test passed: User profile updated successfully.");
    }

    @Test
    @Order(10)
    @DisplayName("It should return 404 when updating non-existent user profile")
    void updateUserProfile_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when updating non-existent user profile.");

        mockUserProfileDTO.setBiography("Updated Biography");

        mockMvc.perform(put("/api/user-profiles/update/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockUserProfileDTO)))
                .andExpect(status().isNotFound());

        logger.info("Test passed: No user profile found to update.");
    }

    @Test
    @Order(11)
    @DisplayName("It should delete user profile successfully")
    void deleteUserProfile_success() throws Exception {
        logger.info("Starting test: It should delete user profile successfully.");

        mockMvc.perform(delete("/api/user-profiles/delete/{id}", mockUserProfileEntity.getId()))
                .andExpect(status().isNoContent());

        assertThat(userProfileRepository.existsById(mockUserProfileEntity.getId())).isFalse();

        logger.info("Test passed: User profile deleted successfully.");
    }

    @Test
    @Order(12)
    @DisplayName("It should return 404 when deleting non-existent user profile")
    void deleteUserProfile_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when deleting non-existent user profile.");

        mockMvc.perform(delete("/api/user-profiles/delete/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Test passed: No user profile found to delete.");
    }

    private void initializeMockData() {
        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setUsername("TestUser3");
        mockUserEntity.setEmail("test3@example.com");
        mockUserEntity.setPassword("Valid3_Password!123");

        mockUserProfileEntity = new UserProfileEntity();
        mockUserProfileEntity.setBiography("Test Biography");
        mockUserProfileEntity.setDateOfBirth(LocalDate.of(2000, 8, 14));
        mockUserProfileEntity.setGender(Gender.MALE);
        mockUserProfileEntity.setLocation("Test Location");
        mockUserProfileEntity.setUser(mockUserEntity);

        mockUserProfileEntity = userProfileRepository.save(mockUserProfileEntity);

        mockUserProfileDTO = new UserProfileDTO();
        mockUserProfileDTO.setId(mockUserProfileEntity.getId());
        mockUserProfileDTO.setBiography(mockUserProfileEntity.getBiography());
        mockUserProfileDTO.setDateOfBirth(String.valueOf(mockUserProfileEntity.getDateOfBirth()));
        mockUserProfileDTO.setGender(mockUserProfileEntity.getGender());
        mockUserProfileDTO.setLocation(mockUserProfileEntity.getLocation());
    }
}