package com.batubook.backend.Tests.UserTests;

import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeAll
    static void beforeAll() {
        logger.info("Test suite started.");
    }

    @BeforeEach
    void setup() {
        logger.info("Setting up initial user entity");
        user = createTestUser();
        userRepository.saveAndFlush(user);
    }

    @AfterEach
    void cleanUp() {
        logger.info("Cleaning up database after test");
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        logger.info("Test suite completed.");
    }

    private UserEntity createTestUser() {
        UserEntity user = UserEntity.builder()
                .username("bbatuhan")
                .email("bbatuhan@batubook.com")
                .password("validPassword!123")
                .role(Role.ADMIN)
                .build();

        UserProfileEntity userProfile = UserProfileEntity.builder()
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

        user.setUserProfile(userProfile);
        return user;
    }

    @Test
    @Order(1)
    @DisplayName("Should save and retrieve all user fields correctly")
    void shouldSaveAndRetrieveAllFields() {
        logger.info("Running test: Should save and retrieve all user fields correctly");

        Optional<UserEntity> foundUser = userRepository.findById(user.getId());

        assertThat(foundUser).isPresent();
        UserEntity users = foundUser.get();

        assertAll("Check all fields",
                () -> assertThat(users.getUsername()).isEqualTo("bbatuhan"),
                () -> assertThat(users.getEmail()).isEqualTo("bbatuhan@batubook.com"),
                () -> assertThat(users.getUserProfile().getBiography()).isEqualTo("Full Stack Software Developer"),
                () -> assertThat(users.getUserProfile().getLocation()).isEqualTo("Istanbul"),
                () -> assertThat(users.getUserProfile().getDateOfBirth()).isEqualTo(LocalDate.of(2000, 8, 14)),
                () -> assertThat(users.getUserProfile().getGender()).isEqualTo(Gender.MALE),
                () -> assertThat(users.getUserProfile().getProfileImageUrl()).isEqualTo("https://batubook.com/profile.jpg"),
                () -> assertThat(users.getUserProfile().getEducation()).isEqualTo("Computer Engineering"),
                () -> assertThat(users.getUserProfile().getOccupation()).isEqualTo("Software Developer"),
                () -> assertThat(users.getUserProfile().getInterests()).isEqualTo("Coding, Reading, Music")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should trim string fields on save")
    void shouldTrimStringFields() {
        logger.info("Running test: Should trim string fields on save");

        UserEntity userEntity = userRepository.findById(user.getId()).orElseThrow();

        assertAll("Trimmed Fields",
                () -> assertThat(userEntity.getUsername()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userEntity.getEmail()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(userEntity.getPassword()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(user.getRole()).isNotNull(),
                () -> assertThat(user.getRole()).isEqualTo(Role.ADMIN)
        );
    }

    @Test
    @Order(3)
    @DisplayName("Should find user by username and email, ignoring case")
    void shouldFindUserByUsernameAndEmailIgnoreCase() {
        logger.info("Running test: Should find user by username and email, ignoring case");

        Optional<UserEntity> foundUser = userRepository
                .findByUsernameAndEmailIgnoreCase("bbatuhan", "bbatuhan@batubook.com", Pageable.unpaged())
                .getContent()
                .stream()
                .findFirst();

        assertAll("User found and validated",
                () -> assertThat(foundUser).isPresent(),
                () -> assertThat(foundUser.get().getUsername()).isEqualTo("bbatuhan"),
                () -> assertThat(foundUser.get().getEmail()).isEqualTo("bbatuhan@batubook.com")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should find users by role")
    void shouldFindUsersByRole() {
        logger.info("Running test: Should find users by role");

        Page<UserEntity> foundUsers = userRepository.findByRole(Role.ADMIN, Pageable.unpaged());

        assertAll("Users with ADMIN role",
                () -> assertThat(foundUsers.getContent()).isNotEmpty(),
                () -> assertThat(foundUsers.getContent())
                        .allMatch(user -> user.getRole() == Role.ADMIN)
        );
    }

    @Test
    @Order(5)
    @DisplayName("Should return empty when user is not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        logger.info("Running test: Should return empty when user is not found by ID");

        Long nonExistentId = 999L;
        Optional<UserEntity> foundUser = userRepository.findById(nonExistentId);
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @Order(6)
    @DisplayName("Should save and delete user correctly")
    void shouldSaveAndDeleteUser() {
        logger.info("Running test: Should save and delete user correctly");

        UserEntity userToDelete = UserEntity.builder()
                .username("deleteMe")
                .email("deleteMe@batubook.com")
                .password("password!123P")
                .role(Role.USER)
                .build();

        userRepository.save(userToDelete);
        userRepository.deleteById(userToDelete.getId());

        Optional<UserEntity> foundUser = userRepository.findById(userToDelete.getId());
        assertThat(foundUser).isNotPresent();
    }

    @Test
    @Order(7)
    @DisplayName("Should update user profile correctly")
    void shouldUpdateUserProfile() {
        logger.info("Running test: Should update user profile correctly");

        UserEntity userToUpdate = userRepository.save(user);
        UserProfileEntity updatedProfile = userToUpdate.getUserProfile();
        updatedProfile.setBiography("Updated biography!");
        userRepository.save(userToUpdate);

        Optional<UserEntity> foundUser = userRepository.findById(userToUpdate.getId());

        assertAll("User profile updated",
                () -> assertThat(foundUser).isPresent(),
                () -> assertThat(foundUser.get().getUserProfile().getBiography())
                        .isEqualTo("Updated biography!")
        );
    }

    @Test
    @Order(8)
    @DisplayName("Should return empty list when no users are found")
    void shouldReturnEmptyListWhenNoUsersFound() {
        logger.info("Running test: Should return empty list when no users are found");

        userRepository.deleteAll();
        Page<UserEntity> users = userRepository.findByRole(Role.USER, Pageable.unpaged());
        assertThat(users).isEmpty();
    }

    @Test
    @Order(9)
    @DisplayName("Should paginate and sort users correctly")
    void shouldPaginateAndSortUsersCorrectly() {
        logger.info("Running test: Should paginate and sort users correctly");

        userRepository.saveAll(List.of(
                UserEntity.builder().username("user1").email("user1@batubook.com").password("password!1A").role(Role.USER).build(),
                UserEntity.builder().username("user2").email("user2@batubook.com").password("password!2B").role(Role.USER).build()
        ));

        Pageable pageable = PageRequest.of(0, 1, Sort.by("username").ascending());
        Page<UserEntity> usersPage = userRepository.findByRole(Role.USER, pageable);

        assertAll("Pagination and sorting",
                () -> assertThat(usersPage.getContent()).hasSize(1),
                () -> assertThat(usersPage.getContent().get(0).getUsername()).isEqualTo("user1")
        );
    }
}