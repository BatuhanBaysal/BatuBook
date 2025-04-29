package com.batubook.backend.Tests.FollowTests;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.FollowEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.FollowRepository;
import com.batubook.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(FollowRepositoryTest.class);

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private UserEntity user;
    private BookEntity book;

    @BeforeEach
    void setUp() {
        logger.info("Creating test data...");

        user = UserEntity.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("Test1234!")
                .role(Role.USER)
                .build();

        UserProfileEntity profile = UserProfileEntity.builder()
                .user(user)
                .dateOfBirth(LocalDate.now().minusYears(25))
                .biography("Test biography")
                .location("Ankara")
                .gender(Gender.MALE)
                .build();

        user.setUserProfile(profile);
        user = userRepository.save(user);

        book = BookEntity.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9876543210")
                .genre(Genre.SCIENCE_FICTION)
                .publishDate(LocalDate.now().minusYears(1))
                .pageCount(300)
                .summary("Test summary")
                .build();

        book = bookRepository.save(book);
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up test data...");
        followRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should create a follow for a user successfully")
    void testCreateFollowForUser_ShouldPersistSuccessfully() {
        UserEntity anotherUser = UserEntity.builder()
                .username("anotheruser")
                .email("anotheruser@example.com")
                .password("Test1234!")
                .role(Role.USER)
                .build();

        UserProfileEntity anotherUserProfile = UserProfileEntity.builder()
                .user(anotherUser)
                .dateOfBirth(LocalDate.now().minusYears(30))
                .biography("Another user's biography")
                .location("Istanbul")
                .gender(Gender.FEMALE)
                .build();

        anotherUser.setUserProfile(anotherUserProfile);
        anotherUser = userRepository.save(anotherUser);

        FollowEntity follow = FollowEntity.builder()
                .follower(user)
                .followedUser(anotherUser)
                .build();

        FollowEntity savedFollow = followRepository.save(follow);

        assertThat(savedFollow).isNotNull();
        assertThat(savedFollow.getId()).isNotNull();
        assertThat(savedFollow.getFollower()).isEqualTo(user);
        assertThat(savedFollow.getFollowedUser()).isEqualTo(anotherUser);
        assertThat(savedFollow.getFollowedBook()).isNull();
    }

    @Test
    @Order(2)
    @DisplayName("Should create a follow for a book successfully")
    void testCreateFollowForBook_ShouldPersistSuccessfully() {
        FollowEntity follow = FollowEntity.builder()
                .follower(user)
                .followedBook(book)
                .build();

        FollowEntity savedFollow = followRepository.save(follow);

        assertThat(savedFollow).isNotNull();
        assertThat(savedFollow.getId()).isNotNull();
        assertThat(savedFollow.getFollower()).isEqualTo(user);
        assertThat(savedFollow.getFollowedBook()).isEqualTo(book);
        assertThat(savedFollow.getFollowedUser()).isNull();
    }

    @Test
    @Order(3)
    @DisplayName("Should return null when trying to find a follow for a non-existing user")
    void testFindByFollowerAndFollowedUser_ShouldReturnNull_WhenFollowDoesNotExist() {
        UserEntity nonExistentUser = UserEntity.builder()
                .username("nonexistentuser")
                .email("nonexistentuser@example.com")
                .password("Test1234!")
                .role(Role.USER)
                .build();

        UserProfileEntity nonExistentUserProfile = UserProfileEntity.builder()
                .user(nonExistentUser)
                .dateOfBirth(LocalDate.now().minusYears(30))
                .biography("Nonexistent user's biography")
                .location("Unknown Location")
                .gender(Gender.MALE)
                .build();

        nonExistentUser.setUserProfile(nonExistentUserProfile);
        nonExistentUser = userRepository.save(nonExistentUser);
        FollowEntity result = followRepository.findByFollowerAndFollowedUser(user, nonExistentUser);

        assertThat(result).isNull();
    }

    @Test
    @Order(4)
    @DisplayName("Should return null when trying to find a follow for a non-existing book")
    void testFindByFollowerAndFollowedBook_ShouldReturnNull_WhenFollowDoesNotExist() {
        BookEntity nonExistentBook = BookEntity.builder()
                .title("Nonexistent Book")
                .author("Unknown Author")
                .isbn("0000000000")
                .genre(Genre.SCIENCE_FICTION)
                .publishDate(LocalDate.now().minusYears(25))
                .pageCount(300)
                .summary("This book doesn't exist")
                .build();

        nonExistentBook = bookRepository.save(nonExistentBook);
        FollowEntity result = followRepository.findByFollowerAndFollowedBook(user, nonExistentBook);

        assertThat(result).isNull();
    }

    @Test
    @Order(5)
    @DisplayName("Should delete a follow for a user successfully")
    void testDeleteFollowForUser_ShouldRemoveSuccessfully() {
        UserEntity anotherUser = UserEntity.builder()
                .username("anotheruser")
                .email("anotheruser@example.com")
                .password("Test1234!")
                .role(Role.USER)
                .build();

        UserProfileEntity anotherUserProfile = UserProfileEntity.builder()
                .user(anotherUser)
                .dateOfBirth(LocalDate.now().minusYears(30))
                .biography("Another user's biography")
                .location("Istanbul")
                .gender(Gender.FEMALE)
                .build();

        anotherUser.setUserProfile(anotherUserProfile);
        anotherUser = userRepository.save(anotherUser);

        FollowEntity follow = FollowEntity.builder()
                .follower(user)
                .followedUser(anotherUser)
                .build();

        FollowEntity savedFollow = followRepository.save(follow);
        followRepository.delete(savedFollow);
        FollowEntity deletedFollow = followRepository.findByFollowerAndFollowedUser(user, anotherUser);

        assertThat(deletedFollow).isNull();
    }

    @Test
    @Order(6)
    @DisplayName("Should delete a follow for a book successfully")
    void testDeleteFollowForBook_ShouldRemoveSuccessfully() {
        FollowEntity follow = FollowEntity.builder()
                .follower(user)
                .followedBook(book)
                .build();

        FollowEntity savedFollow = followRepository.save(follow);
        followRepository.delete(savedFollow);
        FollowEntity deletedFollow = followRepository.findByFollowerAndFollowedBook(user, book);

        assertThat(deletedFollow).isNull();
    }
}