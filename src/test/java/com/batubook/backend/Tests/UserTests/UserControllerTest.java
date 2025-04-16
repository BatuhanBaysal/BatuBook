package com.batubook.backend.Tests.UserTests;

import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.UserRepository;
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
public class UserControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private UserDTO mockUserDTO;
    private UserEntity mockUserEntity;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for a single test...");
        userRepository.deleteAll();
        initializeMockData();
        logger.info("Test data initialized successfully.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Test completed, no database changes should remain due to @Transactional.");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("All tests completed. Closing resources...");
    }

    @Test
    @Order(1)
    @DisplayName("It should create a user successfully with valid data")
    void createUser_success() throws Exception {
        logger.info("Starting test: It should create a user successfully with valid data.");

        UserDTO newUserDTO = new UserDTO();
        newUserDTO.setUsername("uniqueuser123");
        newUserDTO.setEmail("uniqueuser123@example.com");
        newUserDTO.setPassword("securePassword!456");
        newUserDTO.setRole(Role.USER);

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setDateOfBirth("1992-04-15");
        profileDTO.setBiography("Full stack developer");
        profileDTO.setLocation("Ankara");
        profileDTO.setOccupation("Developer");
        profileDTO.setEducation("Master's Degree");
        profileDTO.setInterests("Coding, Hiking");
        profileDTO.setProfileImageUrl("http://image.url/anotherProfile.jpg");
        profileDTO.setGender(Gender.FEMALE);

        newUserDTO.setUserProfile(profileDTO);

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(newUserDTO.getUsername()))
                .andExpect(jsonPath("$.email").value(newUserDTO.getEmail()))
                .andExpect(jsonPath("$.userProfile.biography").value("Full stack developer"));

        logger.info("Test passed: User successfully created with username: {}", newUserDTO.getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("It should fetch a user by ID successfully")
    void fetchUserById_success() throws Exception {
        logger.info("Starting test: It should fetch a user by ID successfully.");

        mockMvc.perform(get("/api/users/{id}", mockUserEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserEntity.getId()))
                .andExpect(jsonPath("$.username").value("testbatu"))
                .andExpect(jsonPath("$.email").value("testbatu@example.com"));

        logger.info("Test passed: User fetched successfully with ID: {}", mockUserEntity.getId());
    }

    @Test
    @Order(3)
    @DisplayName("It should return 404 when user not found by ID")
    void fetchUserById_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when user not found by ID.");

        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Test passed: No user found with the provided ID.");
    }

    @Test
    @Order(4)
    @DisplayName("It should fetch all users with pagination")
    void fetchAllUsers_success() throws Exception {
        logger.info("Starting test: It should fetch all users with pagination.");

        mockMvc.perform(get("/api/users?page=0&size=5&sort=id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].username").value(mockUserEntity.getUsername()))
                .andExpect(jsonPath("$.content[0].email").value(mockUserEntity.getEmail()));

        logger.info("Test passed: Users fetched with pagination.");
    }

    @Test
    @Order(5)
    @DisplayName("It should return 404 when searching users by non-existent username and email")
    void fetchUsersByUsernameAndEmail_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when searching users by non-existent username and email.");

        mockMvc.perform(get("/api/users/search-username-email")
                        .param("username", "NonExistent")
                        .param("email", "nonexistent@example.com")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        logger.info("Test passed: No users found for the provided username and email.");
    }

    @Test
    @Order(6)
    @DisplayName("It should fetch users by role")
    void fetchUsersByRole_success() throws Exception {
        logger.info("Starting test: It should fetch users by role.");

        mockMvc.perform(get("/api/users/search-role")
                        .param("role", "USER")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].role").value("user"));

        logger.info("Test passed: Users fetched successfully by role.");
    }

    @Test
    @Order(7)
    @DisplayName("It should update user successfully")
    void updateUser_success() throws Exception {
        logger.info("Starting test: It should update user successfully.");

        mockUserDTO.setUsername("UpdatedUser");

        UserProfileDTO updatedProfile = new UserProfileDTO();
        updatedProfile.setDateOfBirth("1990-01-01");
        updatedProfile.setBiography("Updated bio");
        updatedProfile.setLocation("Istanbul");
        updatedProfile.setOccupation("Engineer");
        updatedProfile.setEducation("University");
        updatedProfile.setInterests("Coding");
        updatedProfile.setProfileImageUrl("http://image.url/profile.jpg");
        updatedProfile.setGender(Gender.MALE);

        mockUserDTO.setUserProfile(updatedProfile);

        mockMvc.perform(put("/api/users/update/{id}", mockUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("UpdatedUser"));

        logger.info("Test passed: User updated successfully with username: {}", mockUserDTO.getUsername());
    }

    @Test
    @Order(8)
    @DisplayName("It should return 404 when updating non-existent user")
    void updateUser_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when updating non-existent user.");

        mockMvc.perform(put("/api/users/update/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockUserDTO)))
                .andExpect(status().isNotFound());

        logger.info("Test passed: No user found to update.");
    }

    @Test
    @Order(9)
    @DisplayName("It should delete user successfully")
    void deleteUser_success() throws Exception {
        logger.info("Starting test: It should delete user successfully.");

        mockMvc.perform(delete("/api/users/delete/{id}", mockUserEntity.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(mockUserEntity.getId())).isFalse();

        logger.info("Test passed: User deleted successfully with ID: {}", mockUserEntity.getId());
    }

    @Test
    @Order(10)
    @DisplayName("It should return 404 when deleting non-existent user")
    void deleteUser_notFound() throws Exception {
        logger.info("Starting test: It should return 404 when deleting non-existent user.");

        mockMvc.perform(delete("/api/users/delete/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Test passed: No user found to delete.");
    }

    private void initializeMockData() {
        mockUserDTO = new UserDTO();
        mockUserDTO.setUsername("testbatu");
        mockUserDTO.setEmail("testbatu@example.com");
        mockUserDTO.setPassword("securePassword!123");
        mockUserDTO.setRole(Role.USER);

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setDateOfBirth("1990-01-01");
        profileDTO.setBiography("Software developer");
        profileDTO.setLocation("Istanbul");
        profileDTO.setOccupation("Engineer");
        profileDTO.setEducation("University");
        profileDTO.setInterests("Coding, Reading");
        profileDTO.setProfileImageUrl("http://image.url/profile.jpg");
        profileDTO.setGender(Gender.MALE);

        mockUserDTO.setUserProfile(profileDTO);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(mockUserDTO.getUsername());
        userEntity.setEmail(mockUserDTO.getEmail());
        userEntity.setPassword("securePassword!123");
        userEntity.setRole(Role.USER);

        UserProfileEntity profileEntity = new UserProfileEntity();
        profileEntity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        profileEntity.setBiography("Software developer");
        profileEntity.setLocation("Istanbul");
        profileEntity.setOccupation("Engineer");
        profileEntity.setEducation("University");
        profileEntity.setInterests("Coding, Reading");
        profileEntity.setProfileImageUrl("http://image.url/profile.jpg");
        profileEntity.setGender(Gender.MALE);

        userEntity.setUserProfile(profileEntity);
        profileEntity.setUser(userEntity);

        mockUserEntity = userRepository.save(userEntity);
    }
}