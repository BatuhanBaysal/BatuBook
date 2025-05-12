package com.batubook.backend.Tests.UserProfileTests;

import com.batubook.backend.dto.UserProfileDTO;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.repository.UserProfileRepository;
import com.batubook.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserProfileSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    private UserProfileDTO mockUserProfileDTO;

    @BeforeEach
    void setup() {
        userProfileRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity newUser = new UserEntity();
        newUser.setUsername("testuser");
        newUser.setEmail("testuser@example.com");
        newUser.setPassword("testpassword!123A");
        userRepository.save(newUser);

        UserProfileEntity newUserProfile = new UserProfileEntity();
        newUserProfile.setDateOfBirth(LocalDate.parse("2000-08-14"));
        newUserProfile.setGender(Gender.MALE);
        newUserProfile.setProfileImageUrl("mock/image/path/test.jpg");
        newUserProfile.setBiography("Test Biography");
        newUserProfile.setLocation("Izmir");
        newUserProfile.setUser(newUser);

        userProfileRepository.save(newUserProfile);

        mockUserProfileDTO = new UserProfileDTO();
        mockUserProfileDTO.setId(newUserProfile.getId());
        mockUserProfileDTO.setDateOfBirth("2000-08-14");
        mockUserProfileDTO.setGender(Gender.MALE);
        mockUserProfileDTO.setProfileImageUrl("mock/image/path/test.jpg");
        mockUserProfileDTO.setBiography("Test Biography");
        mockUserProfileDTO.setLocation("Izmir");
    }

    @Test
    @Order(1)
    @DisplayName("Test XSS vulnerability - User Profile update with malicious script")
    void updateUserProfileWithXSS_ShouldReturnSafeResponse() throws Exception {
        mockUserProfileDTO.setBiography("Test Biography");

        mockMvc.perform(
                        put("/api/user-profiles/update/{id}", mockUserProfileDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(mockUserProfileDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("<script>alert('XSS Attack');</script>"))));
    }
}