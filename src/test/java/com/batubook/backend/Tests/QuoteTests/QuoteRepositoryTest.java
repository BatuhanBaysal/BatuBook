package com.batubook.backend.Tests.QuoteTests;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.QuoteEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.QuoteRepository;
import com.batubook.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuoteRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteRepositoryTest.class);

    @Autowired
    private QuoteRepository quoteRepository;

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
        quoteRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should persist the quote successfully")
    void testSaveQuote_ShouldPersistSuccessfully() {
        QuoteEntity quote = QuoteEntity.builder()
                .quoteText("This is a memorable quote.")
                .user(user)
                .book(book)
                .build();

        QuoteEntity savedQuote = quoteRepository.save(quote);

        assertThat(savedQuote).isNotNull();
        assertThat(savedQuote.getId()).isNotNull();
        assertThat(savedQuote.getQuoteText()).isEqualTo("This is a memorable quote.");
        assertThat(savedQuote.getUser()).isEqualTo(user);
        assertThat(savedQuote.getBook()).isEqualTo(book);
    }

    @Test
    @Order(2)
    @DisplayName("Should trim quote text before saving")
    void testSavedQuote_ShouldTrimQuoteText() {
        QuoteEntity quote = QuoteEntity.builder()
                .quoteText(" Inspirational quote. ")
                .user(user)
                .book(book)
                .build();

        QuoteEntity savedQuote = quoteRepository.save(quote);

        assertThat(savedQuote.getQuoteText()).isEqualTo("Inspirational quote.");
    }

    @Test
    @Order(3)
    @DisplayName("Should return empty when quote does not exist")
    void testFindById_ShouldReturnEmpty_WhenQuoteDoesNotExist() {
        Optional<QuoteEntity> result = quoteRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Should remove quote successfully when deleted")
    void testDeleteQuote_ShouldRemoveSuccessfully() {
        QuoteEntity quote = QuoteEntity.builder()
                .quoteText("Temporary quote for deletion.")
                .user(user)
                .book(book)
                .build();

        quote = quoteRepository.save(quote);

        quoteRepository.delete(quote);
        Optional<QuoteEntity> result = quoteRepository.findById(quote.getId());

        assertThat(result).isEmpty();
    }
}