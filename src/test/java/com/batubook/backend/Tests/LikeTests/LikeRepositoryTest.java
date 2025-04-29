package com.batubook.backend.Tests.LikeTests;

import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.MessageType;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LikeRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(LikeRepositoryTest.class);

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookInteractionRepository bookInteractionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private MessageRepository messageRepository;

    private UserEntity sender;
    private UserEntity receiver;
    private BookEntity book;
    private BookInteractionEntity interaction;
    private ReviewEntity review;
    private QuoteEntity quote;
    private MessageEntity message;

    @BeforeEach
    void setUp() {
        logger.info("Creating test data...");

        sender = UserEntity.builder()
                .username("sender")
                .email("sender@example.com")
                .password("Sender1234!")
                .role(Role.USER)
                .build();

        receiver = UserEntity.builder()
                .username("receiver")
                .email("receiver@example.com")
                .password("Receiver1234!")
                .role(Role.USER)
                .build();

        userRepository.saveAll(List.of(sender, receiver));

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

        interaction = BookInteractionEntity.builder()
                .user(sender)
                .book(book)
                .isRead(true)
                .isLiked(true)
                .description("A liked book.")
                .build();
        interaction = bookInteractionRepository.save(interaction);

        review = ReviewEntity.builder()
                .reviewText("Very good book!")
                .rating(new java.math.BigDecimal("4.5"))
                .book(book)
                .user(sender)
                .build();
        review = reviewRepository.save(review);

        quote = QuoteEntity.builder()
                .quoteText("This is a quote")
                .book(book)
                .user(sender)
                .build();
        quote = quoteRepository.save(quote);

        message = MessageEntity.builder()
                .messageContent("This is a message")
                .sender(sender)
                .receiver(receiver)
                .messageType(MessageType.PERSONAL)
                .build();
        message = messageRepository.save(message);
    }

    @Test
    @Order(1)
    @DisplayName("Should create a like for a book interaction successfully")
    void testCreateLikeForBookInteraction_ShouldPersistSuccessfully() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .review(null)
                .quote(null)
                .message(null)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getId()).isNotNull();
        assertThat(savedLike.getUser().getId()).isEqualTo(sender.getId());
        assertThat(savedLike.getBookInteraction().getId()).isEqualTo(interaction.getId());
        assertThat(savedLike.getReview()).isNull();
        assertThat(savedLike.getQuote()).isNull();
        assertThat(savedLike.getMessage()).isNull();
    }

    @Test
    @Order(2)
    @DisplayName("Should create a like for a review successfully")
    void testCreateLikeForReview_ShouldPersistSuccessfully() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .review(review)
                .bookInteraction(null)
                .quote(null)
                .message(null)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getId()).isNotNull();
        assertThat(savedLike.getUser().getId()).isEqualTo(sender.getId());
        assertThat(savedLike.getReview().getId()).isEqualTo(review.getId());
        assertThat(savedLike.getBookInteraction()).isNull();
        assertThat(savedLike.getQuote()).isNull();
        assertThat(savedLike.getMessage()).isNull();
    }

    @Test
    @Order(3)
    @DisplayName("Should create a like for a quote successfully")
    void testCreateLikeForQuote_ShouldPersistSuccessfully() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .quote(quote)
                .bookInteraction(null)
                .review(null)
                .message(null)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getId()).isNotNull();
        assertThat(savedLike.getUser().getId()).isEqualTo(sender.getId());
        assertThat(savedLike.getQuote().getId()).isEqualTo(quote.getId());
        assertThat(savedLike.getBookInteraction()).isNull();
        assertThat(savedLike.getReview()).isNull();
        assertThat(savedLike.getMessage()).isNull();
    }

    @Test
    @Order(4)
    @DisplayName("Should create a like for a message successfully")
    void testCreateLikeForMessage_ShouldPersistSuccessfully() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .message(message)
                .bookInteraction(null)
                .review(null)
                .quote(null)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        assertThat(savedLike).isNotNull();
        assertThat(savedLike.getId()).isNotNull();
        assertThat(savedLike.getUser().getId()).isEqualTo(sender.getId());
        assertThat(savedLike.getMessage().getId()).isEqualTo(message.getId());
        assertThat(savedLike.getBookInteraction()).isNull();
        assertThat(savedLike.getReview()).isNull();
        assertThat(savedLike.getQuote()).isNull();
    }

    @Test
    @Order(5)
    @DisplayName("Should throw an exception when trying to create a like for multiple relations")
    void testCreateLikeForMultipleRelations_ShouldThrowException() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .review(review)
                .quote(quote)
                .message(message)
                .build();

        assertThatThrownBy(() -> likeRepository.save(like))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("Only one type of like can be selected.");
    }

    @Test
    @Order(6)
    @DisplayName("Should return true if a like exists for a message")
    void testExistsByUserIdAndMessageId_ShouldReturnTrueIfLikeExists() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .message(message)
                .bookInteraction(null)
                .review(null)
                .quote(null)
                .build();

        likeRepository.save(like);

        boolean exists = likeRepository.existsByUserIdAndMessageId(sender.getId(), message.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @Order(7)
    @DisplayName("Should return false if a like does not exist for a message")
    void testExistsByUserIdAndMessageId_ShouldReturnFalseIfLikeDoesNotExist() {
        boolean exists = likeRepository.existsByUserIdAndMessageId(sender.getId(), message.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @Order(8)
    @DisplayName("Should return true if a like exists for a book interaction")
    void testExistsByUserIdAndBookInteractionId_ShouldReturnTrueIfLikeExists() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .review(null)
                .quote(null)
                .message(null)
                .build();

        likeRepository.save(like);

        boolean exists = likeRepository.existsByUserIdAndBookInteractionId(sender.getId(), interaction.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @Order(9)
    @DisplayName("Should return false if a like does not exist for a book interaction")
    void testExistsByUserIdAndBookInteractionId_ShouldReturnFalseIfLikeDoesNotExist() {
        boolean exists = likeRepository.existsByUserIdAndBookInteractionId(sender.getId(), interaction.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @Order(10)
    @DisplayName("Should return true if a like exists for a review")
    void testExistsByUserIdAndReviewId_ShouldReturnTrueIfLikeExists() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .review(review)
                .bookInteraction(null)
                .quote(null)
                .message(null)
                .build();

        likeRepository.save(like);

        boolean exists = likeRepository.existsByUserIdAndReviewId(sender.getId(), review.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @Order(11)
    @DisplayName("Should return false if a like does not exist for a review")
    void testExistsByUserIdAndReviewId_ShouldReturnFalseIfLikeDoesNotExist() {
        boolean exists = likeRepository.existsByUserIdAndReviewId(sender.getId(), review.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @Order(12)
    @DisplayName("Should return true if a like exists for a quote")
    void testExistsByUserIdAndQuoteId_ShouldReturnTrueIfLikeExists() {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .quote(quote)
                .bookInteraction(null)
                .review(null)
                .message(null)
                .build();

        likeRepository.save(like);

        boolean exists = likeRepository.existsByUserIdAndQuoteId(sender.getId(), quote.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @Order(13)
    @DisplayName("Should return false if a like does not exist for a quote")
    void testExistsByUserIdAndQuoteId_ShouldReturnFalseIfLikeDoesNotExist() {
        boolean exists = likeRepository.existsByUserIdAndQuoteId(sender.getId(), quote.getId());

        assertThat(exists).isFalse();
    }
}