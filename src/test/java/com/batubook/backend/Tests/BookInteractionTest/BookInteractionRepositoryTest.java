package com.batubook.backend.Tests.BookInteractionTest;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.BookInteractionEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookInteractionRepository;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookInteractionRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(BookInteractionRepositoryTest.class);

    @Autowired
    private BookInteractionRepository bookInteractionRepository;

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

        BookInteractionEntity interaction = BookInteractionEntity.builder()
                .user(user)
                .book(book)
                .isRead(true)
                .isLiked(true)
                .description("A liked book.")
                .build();

        bookInteractionRepository.save(interaction);
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up test data...");
        bookInteractionRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should find interactions by user ID where isRead is true")
    void shouldFindByUserIdAndIsReadTrue() {
        Page<BookInteractionEntity> result = bookInteractionRepository.findByUserIdAndIsReadTrue(user.getId(), Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(result.getContent().get(0).getIsRead()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Should find interactions by user ID where isLiked is true")
    void shouldFindByUserIdAndIsLikedTrue() {
        Page<BookInteractionEntity> result = bookInteractionRepository.findByUserIdAndIsLikedTrue(user.getId(), Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsLiked()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Should find interactions by book ID where isRead is true")
    void shouldFindByBookIdAndIsReadTrue() {
        Page<BookInteractionEntity> result = bookInteractionRepository.findByBookIdAndIsReadTrue(book.getId(), Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBook().getId()).isEqualTo(book.getId());
        assertThat(result.getContent().get(0).getIsRead()).isTrue();
    }

    @Test
    @Order(4)
    @DisplayName("Should find interactions by book ID where isLiked is true")
    void shouldFindByBookIdAndIsLikedTrue() {
        Page<BookInteractionEntity> result = bookInteractionRepository.findByBookIdAndIsLikedTrue(book.getId(), Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsLiked()).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("Should verify existence of interaction where user has read the book")
    void shouldExistByUserIdAndBookIdAndIsReadTrue() {
        boolean exists = bookInteractionRepository.existsByUserIdAndBookIdAndIsReadTrue(user.getId(), book.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @Order(6)
    @DisplayName("Should verify existence of interaction where user liked the book")
    void shouldExistByUserIdAndBookIdAndIsLikedTrue() {
        boolean exists = bookInteractionRepository.existsByUserIdAndBookIdAndIsLikedTrue(user.getId(), book.getId());

        assertThat(exists).isTrue();
    }
}