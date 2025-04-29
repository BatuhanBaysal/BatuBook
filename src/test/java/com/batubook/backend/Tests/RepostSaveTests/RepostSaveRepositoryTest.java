package com.batubook.backend.Tests.RepostSaveTests;

import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.ActionType;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepostSaveRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(RepostSaveRepositoryTest.class);

    @Autowired
    private RepostSaveRepository repostSaveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private BookInteractionRepository bookInteractionRepository;

    private UserEntity user;
    private ReviewEntity review;
    private QuoteEntity quote;
    private BookInteractionEntity bookInteraction;

    @BeforeEach
    void setUp() {
        logger.info("Creating test data...");

        user = UserEntity.builder()
                .username("testUser")
                .email("testuser@example.com")
                .password("Test1234!")
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        BookEntity book = BookEntity.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9876543210")
                .genre(Genre.SCIENCE_FICTION)
                .publishDate(LocalDate.now().minusYears(1))
                .pageCount(300)
                .summary("Test summary")
                .build();
        book = bookRepository.save(book);

        review = ReviewEntity.builder()
                .reviewText("Amazing book!")
                .rating(new BigDecimal("4.5"))
                .book(book)
                .user(user)
                .build();
        review = reviewRepository.save(review);

        quote = QuoteEntity.builder()
                .quoteText("This is a quote from the book")
                .book(book)
                .user(user)
                .build();
        quote = quoteRepository.save(quote);

        bookInteraction = BookInteractionEntity.builder()
                .user(user)
                .book(book)
                .isRead(true)
                .isLiked(true)
                .description("A book interaction description")
                .build();
        bookInteraction = bookInteractionRepository.save(bookInteraction);
    }

    @Test
    @Order(1)
    @DisplayName("Should find repost saves by user ID")
    void testFindByUserId_ShouldReturnRepostSaves() {
        RepostSaveEntity repostSave = RepostSaveEntity.builder()
                .user(user)
                .review(review)
                .actionType(ActionType.REPOST)
                .build();
        repostSaveRepository.save(repostSave);

        Page<RepostSaveEntity> repostSaves = repostSaveRepository.findByUserId(user.getId(), PageRequest.of(0, 10));

        assertThat(repostSaves).isNotNull();
        assertThat(repostSaves.getContent()).hasSize(1);
        assertThat(repostSaves.getContent().get(0).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Should find repost saves by user ID and action type")
    void testFindByUserIdAndActionType_ShouldReturnRepostSaves() {
        RepostSaveEntity repostSave = RepostSaveEntity.builder()
                .user(user)
                .review(review)
                .actionType(ActionType.REPOST)
                .build();
        repostSaveRepository.save(repostSave);

        Page<RepostSaveEntity> repostSaves = repostSaveRepository.findByUserIdAndActionType(user.getId(), ActionType.REPOST, PageRequest.of(0, 10));

        assertThat(repostSaves).isNotNull();
        assertThat(repostSaves.getContent()).hasSize(1);
        assertThat(repostSaves.getContent().get(0).getActionType()).isEqualTo(ActionType.REPOST);
    }

    @Test
    @Order(3)
    @DisplayName("Should find repost save by user ID and content")
    void testFindByUserIdAndContent_ShouldReturnRepostSave() {
        RepostSaveEntity repostSave = RepostSaveEntity.builder()
                .user(user)
                .review(review)
                .actionType(ActionType.REPOST)
                .build();
        repostSaveRepository.save(repostSave);

        Optional<RepostSaveEntity> repostSaveOptional = repostSaveRepository.findByUserIdAndContent(
                user.getId(),
                review.getId(),
                null,
                null
        );

        assertThat(repostSaveOptional).isPresent();
        assertThat(repostSaveOptional.get().getReview()).isEqualTo(review);
    }

    @Test
    @Order(4)
    @DisplayName("Should return true if repost save exists with user ID and content and action type")
    void testExistsByUserIdAndContentAndActionType_ShouldReturnTrue() {
        RepostSaveEntity repostSave = RepostSaveEntity.builder()
                .user(user)
                .review(review)
                .actionType(ActionType.REPOST)
                .build();
        repostSaveRepository.save(repostSave);

        boolean exists = repostSaveRepository.existsByUserIdAndContentAndActionType(
                user.getId(),
                review.getId(),
                null,
                null,
                ActionType.REPOST
        );

        assertThat(exists).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("Should return false if repost save does not exist with user ID and content and action type")
    void testExistsByUserIdAndContentAndActionType_ShouldReturnFalse() {
        boolean exists = repostSaveRepository.existsByUserIdAndContentAndActionType(
                user.getId(),
                review.getId(),
                null,
                null,
                ActionType.REPOST
        );

        assertThat(exists).isFalse();
    }

    @Test
    @Order(6)
    @DisplayName("Should not find repost save when no content matches")
    void testFindByUserIdAndContent_ShouldReturnEmptyWhenNoContentMatches() {
        Optional<RepostSaveEntity> repostSaveOptional = repostSaveRepository.findByUserIdAndContent(
                user.getId(),
                null,
                null,
                null
        );

        assertThat(repostSaveOptional).isNotPresent();
    }

    @Test
    @Order(7)
    @DisplayName("Should return false if no repost save exists for user ID and content and action type")
    void testExistsByUserIdAndContentAndActionType_ShouldReturnFalseForNonExistent() {
        boolean exists = repostSaveRepository.existsByUserIdAndContentAndActionType(
                user.getId(),
                null,
                null,
                null,
                ActionType.SAVE
        );

        assertThat(exists).isFalse();
    }

    @Test
    @Order(8)
    @DisplayName("Should throw InvalidDataAccessApiUsageException with IllegalStateException message if multiple content types are set")
    void testShouldThrowInvalidDataAccessApiUsageExceptionWhenMultipleContentTypesAreSet() {
        RepostSaveEntity repostSave = RepostSaveEntity.builder()
                .user(user)
                .review(review)
                .quote(quote)
                .actionType(ActionType.SAVE)
                .build();

        InvalidDataAccessApiUsageException thrown = assertThrows(InvalidDataAccessApiUsageException.class, () -> repostSaveRepository.save(repostSave));

        assertThat(thrown.getCause()).isInstanceOf(IllegalStateException.class);
        assertThat(thrown.getCause().getMessage()).contains("Only one content type (Review, Quote, or BookInteraction) can be referenced.");
    }
}