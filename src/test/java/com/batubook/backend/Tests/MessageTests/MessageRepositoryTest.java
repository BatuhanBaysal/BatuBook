package com.batubook.backend.Tests.MessageTests;

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
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(MessageRepositoryTest.class);

    @Autowired
    private MessageRepository messageRepository;

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

    private UserEntity sender;
    private UserEntity receiver;
    private BookEntity book;
    private BookInteractionEntity interaction;
    private ReviewEntity review;
    private QuoteEntity quote;

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

        book = bookRepository.save(BookEntity.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9876543210")
                .genre(Genre.SCIENCE_FICTION)
                .publishDate(LocalDate.now().minusYears(1))
                .pageCount(300)
                .summary("Test summary")
                .build());

        interaction = bookInteractionRepository.save(BookInteractionEntity.builder()
                .user(sender)
                .book(book)
                .isRead(true)
                .isLiked(true)
                .description("Test interaction")
                .build());

        review = reviewRepository.save(ReviewEntity.builder()
                .reviewText("Very good book!")
                .rating(new java.math.BigDecimal("4.5"))
                .user(sender)
                .book(book)
                .build());

        quote = quoteRepository.save(QuoteEntity.builder()
                .quoteText("Important quote.")
                .user(sender)
                .book(book)
                .build());
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up test data...");
        messageRepository.deleteAll();
        quoteRepository.deleteAll();
        reviewRepository.deleteAll();
        bookInteractionRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should save PERSONAL message with sender and receiver only")
    void testSavePersonalMessage() {
        MessageEntity personalMessage = MessageEntity.builder()
                .messageContent(" Hello, how are you? ")
                .messageType(MessageType.PERSONAL)
                .sender(sender)
                .receiver(receiver)
                .build();

        MessageEntity savedMessage = messageRepository.save(personalMessage);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getMessageType()).isEqualTo(MessageType.PERSONAL);
        assertThat(savedMessage.getSender().getId()).isEqualTo(sender.getId());
        assertThat(savedMessage.getReceiver().getId()).isEqualTo(receiver.getId());
        assertThat(savedMessage.getBookInteraction()).isNull();
        assertThat(savedMessage.getReview()).isNull();
        assertThat(savedMessage.getQuote()).isNull();
    }

    @Test
    @Order(2)
    @DisplayName("Should save BOOK message with sender and bookInteraction only")
    void testSaveBookMessage() {
        MessageEntity bookMessage = MessageEntity.builder()
                .messageContent("This is amazing!")
                .messageType(MessageType.BOOK)
                .sender(sender)
                .bookInteraction(interaction)
                .build();

        MessageEntity savedMessage = messageRepository.save(bookMessage);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getMessageType()).isEqualTo(MessageType.BOOK);
        assertThat(savedMessage.getSender().getId()).isEqualTo(sender.getId());
        assertThat(savedMessage.getBookInteraction().getId()).isEqualTo(interaction.getId());
        assertThat(savedMessage.getReceiver()).isNull();
        assertThat(savedMessage.getReview()).isNull();
        assertThat(savedMessage.getQuote()).isNull();
    }

    @Test
    @Order(3)
    @DisplayName("Should save REVIEW message with sender and review only")
    void testSaveReviewMessage() {
        MessageEntity reviewMessage = MessageEntity.builder()
                .messageContent("Great review about this book!")
                .messageType(MessageType.REVIEW)
                .sender(sender)
                .review(review)
                .build();

        MessageEntity savedMessage = messageRepository.save(reviewMessage);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getMessageType()).isEqualTo(MessageType.REVIEW);
        assertThat(savedMessage.getSender().getId()).isEqualTo(sender.getId());
        assertThat(savedMessage.getReview().getId()).isEqualTo(review.getId());
        assertThat(savedMessage.getReceiver()).isNull();
        assertThat(savedMessage.getBookInteraction()).isNull();
        assertThat(savedMessage.getQuote()).isNull();
    }

    @Test
    @Order(4)
    @DisplayName("Should save QUOTE message with sender and quote only")
    void testSaveQuoteMessage() {
        MessageEntity quoteMessage = MessageEntity.builder()
                .messageContent("An inspiring quote from the book.")
                .messageType(MessageType.QUOTE)
                .sender(sender)
                .quote(quote)
                .build();

        MessageEntity savedMessage = messageRepository.save(quoteMessage);

        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getMessageType()).isEqualTo(MessageType.QUOTE);
        assertThat(savedMessage.getSender().getId()).isEqualTo(sender.getId());
        assertThat(savedMessage.getQuote().getId()).isEqualTo(quote.getId());
        assertThat(savedMessage.getReceiver()).isNull();
        assertThat(savedMessage.getBookInteraction()).isNull();
        assertThat(savedMessage.getReview()).isNull();
    }
}