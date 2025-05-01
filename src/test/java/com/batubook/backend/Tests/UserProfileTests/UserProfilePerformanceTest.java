package com.batubook.backend.Tests.UserProfileTests;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfilePerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserProfilePerformanceTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private final List<UserProfileDTO> mockUserProfileDTOList = new ArrayList<>();

    @BeforeEach
    void setupEach() {
        userProfileRepository.deleteAll();
        initializeMockData();
    }

    @Test
    @Order(1)
    @DisplayName("Performance test for fetching all user profiles")
    void fetchAllUserProfiles_PerformanceTest() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        mockMvc.perform(get("/api/user-profiles")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk());

        stopWatch.stop();
        logger.info("Response time for fetching all profiles: {} ms", stopWatch.getTotalTimeMillis());

        assertTrue(stopWatch.getTotalTimeMillis() < 2000);
    }

    @Test
    @Order(2)
    @DisplayName("Performance test for fetching a single user profile by ID")
    void fetchUserProfileById_PerformanceTest() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        mockMvc.perform(get("/api/user-profiles/{id}", mockUserProfileDTOList.get(0).getId()))
                .andExpect(status().isOk());

        stopWatch.stop();
        logger.info("Response time for fetching profile by ID: {} ms", stopWatch.getTotalTimeMillis());

        assertTrue(stopWatch.getTotalTimeMillis() < 1000);
    }

    private void initializeMockData() {
        mockUserProfileDTOList.clear();
        for (int i = 0; i < 100; i++) {
            UserEntity mockUserEntity = createMockUserEntity(i);
            UserProfileEntity mockUserProfileEntity = createMockUserProfileEntity(i, mockUserEntity);
            mockUserProfileEntity = userProfileRepository.save(mockUserProfileEntity);
            UserProfileDTO mockUserProfileDTO = convertEntityToDTO(mockUserProfileEntity);
            mockUserProfileDTOList.add(mockUserProfileDTO);
        }
    }

    private UserEntity createMockUserEntity(int index) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("TestUser" + index);
        userEntity.setEmail("test" + index + "@example.com");
        userEntity.setPassword("Valid_Password_" + index + "!123");
        return userEntity;
    }

    private UserProfileEntity createMockUserProfileEntity(int index, UserEntity userEntity) {
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setBiography("Test Biography " + index);
        userProfileEntity.setDateOfBirth(LocalDate.of(2000, 8, 14));
        userProfileEntity.setGender(Gender.MALE);
        userProfileEntity.setLocation("Test Location " + index);
        userProfileEntity.setProfileImageUrl("mock/image/path/test" + index + ".jpg");
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