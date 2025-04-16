package com.batubook.backend.Tests.UserProfileTests;

import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.UserProfileRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileRepositoryTest.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    private UserProfileEntity userProfile;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder()
                .username("bbatuhan")
                .email("bbatuhan@batubook.com")
                .password("validPassword!123")
                .role(Role.USER)
                .build();

        userProfile = UserProfileEntity.builder()
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

        userProfile.setUser(user);
        userProfileRepository.save(userProfile);
    }

    @AfterEach
    void cleanUp() {
        userProfileRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should save and retrieve all user profile fields correctly")
    void shouldSaveAndRetrieveAllFields() {
        Optional<UserProfileEntity> foundUserProfile = userProfileRepository.findById(userProfile.getId());

        assertThat(foundUserProfile).isPresent();
        UserProfileEntity userProfiles = foundUserProfile.get();

        assertAll("Check all fields",
                () -> assertThat(userProfiles.getUser().getUsername()).isEqualTo("bbatuhan"),
                () -> assertThat(userProfiles.getUser().getEmail()).isEqualTo("bbatuhan@batubook.com"),
                () -> assertThat(userProfiles.getBiography()).isEqualTo("Full Stack Software Developer"),
                () -> assertThat(userProfiles.getLocation()).isEqualTo("Istanbul"),
                () -> assertThat(userProfiles.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 8, 14)),
                () -> assertThat(userProfiles.getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(userProfiles.getProfileImageUrl()).isEqualTo("https://batubook.com/profile.jpg"),
                () -> assertThat(userProfiles.getEducation()).isEqualTo("Computer Engineering"),
                () -> assertThat(userProfiles.getOccupation()).isEqualTo("Software Developer"),
                () -> assertThat(userProfiles.getInterests()).isEqualTo("Coding, Reading, Music")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should trim string fields on save")
    void shouldTrimStringFields() {
        UserProfileEntity userProfileEntity = userProfileRepository.findById(userProfile.getId()).orElseThrow();

        assertAll("Trimmed fields",
                () -> assertThat(userProfileEntity.getDateOfBirth()).isNotNull(),
                () -> assertThat(userProfileEntity.getDateOfBirth()).isBefore(LocalDate.now().minusYears(18)),
                () -> assertThat(userProfileEntity.getProfileImageUrl()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userProfileEntity.getBiography()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userProfileEntity.getLocation()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userProfileEntity.getOccupation()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userProfileEntity.getEducation()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userProfileEntity.getInterests()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userProfile.getGender()).isNotNull(),
                () -> assertThat(userProfile.getGender()).isEqualTo(Gender.MALE)
        );
    }

    @Test
    @Order(3)
    @DisplayName("Should fail to save user profile if underage")
    void shouldFailToSaveUnderageProfile() {
        UserEntity underageUser = UserEntity.builder()
                .username("bbatuhan")
                .email("bbatuhan@example.com")
                .password("validPassword!123")
                .role(Role.USER)
                .build();

        UserProfileEntity underageProfile = UserProfileEntity.builder()
                .user(underageUser)
                .dateOfBirth(LocalDate.parse("2010-08-14"))
                .biography("Too young")
                .gender(Gender.MALE)
                .build();

        assertThatThrownBy(() -> userProfileRepository.save(underageProfile))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("User must be at least 18 years old");
    }

    @Test
    @Order(4)
    @DisplayName("Should handle non-empty optional fields gracefully")
    void shouldHandleNonEmptyOptionalFields() {
        UserEntity user = UserEntity.builder()
                .username("aliveli")
                .email("aliveli@example.com")
                .password("NewvalidPassword!123")
                .role(Role.USER)
                .build();

        UserProfileEntity minimalProfile = UserProfileEntity.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 8, 14))
                .gender(Gender.UNDISCLOSED)
                .biography("Default Biography")
                .location("Default Location")
                .build();

        userProfileRepository.save(minimalProfile);
        Optional<UserProfileEntity> found = userProfileRepository.findById(minimalProfile.getId());

        assertThat(found).isPresent();
        assertAll("Optional fields should have valid values",
                () -> assertThat(found.get().getBiography()).isNotBlank(),
                () -> assertThat(found.get().getLocation()).isNotBlank()
        );
    }

    @Test
    @Order(5)
    @DisplayName("Should update user profile fields")
    void shouldUpdateUserProfileFields() {
        UserProfileEntity profile = userProfileRepository.findById(userProfile.getId()).orElseThrow();

        profile.setBiography("Updated bio");
        profile.setLocation("Ankara");
        profile.setOccupation("Team Lead");

        userProfileRepository.save(profile);

        UserProfileEntity updated = userProfileRepository.findById(profile.getId()).orElseThrow();

        assertAll("Updated fields",
                () -> assertThat(updated.getBiography()).isEqualTo("Updated bio"),
                () -> assertThat(updated.getLocation()).isEqualTo("Ankara"),
                () -> assertThat(updated.getOccupation()).isEqualTo("Team Lead")
        );
    }
}