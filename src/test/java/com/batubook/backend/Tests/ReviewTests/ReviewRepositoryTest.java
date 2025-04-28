package com.batubook.backend.Tests.ReviewTests;

import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.ReviewRepository;
import com.batubook.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReviewRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(ReviewRepositoryTest.class);

    @Autowired
    private ReviewRepository reviewRepository;

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
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should return reviews with the matching rating")
    void testFindByRating_ShouldReturnMatchingReview() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewText("This is a great book!")
                .rating(BigDecimal.valueOf(4.5))
                .user(user)
                .book(book)
                .build();

        reviewRepository.save(review);

        Page<ReviewEntity> result = reviewRepository.findByRating(BigDecimal.valueOf(4.5), PageRequest.of(0, 5));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getReviewText()).isEqualTo("This is a great book!");
    }

    @Test
    @Order(2)
    @DisplayName("Should return an empty page when no matching review is found")
    void testFindByRating_ShouldReturnEmptyPage_WhenNoMatchingReview() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewText("An average book")
                .rating(BigDecimal.valueOf(3.0))
                .user(user)
                .book(book)
                .build();

        reviewRepository.save(review);

        Page<ReviewEntity> result = reviewRepository.findByRating(BigDecimal.valueOf(5.0), PageRequest.of(0, 5));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @Order(3)
    @DisplayName("Should trim the review text before saving")
    void testSave_ShouldTrimReviewText() {
        ReviewEntity review = ReviewEntity.builder()
                .reviewText(" Excellent book! ")
                .rating(BigDecimal.valueOf(5.0))
                .user(user)
                .book(book)
                .build();

        ReviewEntity savedReview = reviewRepository.save(review);

        assertThat(savedReview.getReviewText()).isEqualTo("Excellent book!");
    }
}