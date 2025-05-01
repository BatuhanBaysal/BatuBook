package com.batubook.backend.Tests.LikeTests;

import com.batubook.backend.dto.LikeDTO;
import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.MessageType;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LikeControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(LikeControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    public void setUp() {
        logger.info("Initializing test data for LikeController...");
        likeRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();
        bookInteractionRepository.deleteAll();
        reviewRepository.deleteAll();
        quoteRepository.deleteAll();
        messageRepository.deleteAll();
        initializeMockData();
    }

    @Test
    @Order(1)
    public void testCreateLikeWithBookInteractionSuccessfully() throws Exception {
        LikeDTO likeDTO = LikeDTO.builder()
                .userId(sender.getId())
                .bookInteractionId(interaction.getId())
                .build();

        mockMvc.perform(post("/api/likes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(sender.getId()))
                .andExpect(jsonPath("$.bookInteractionId").value(interaction.getId()));
    }

    @Test
    @Order(2)
    public void testCreateLikeWithMissingField() throws Exception {
        LikeDTO likeDTO = LikeDTO.builder()
                .userId(null)
                .bookInteractionId(interaction.getId())
                .build();

        mockMvc.perform(post("/api/likes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(3)
    public void testFetchLikeByIdSuccessfully() throws Exception {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        mockMvc.perform(get("/api/likes/{id}", savedLike.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLike.getId()))
                .andExpect(jsonPath("$.userId").value(sender.getId()))
                .andExpect(jsonPath("$.bookInteractionId").value(interaction.getId()));
    }

    @Test
    @Order(4)
    public void testFetchLikeByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/likes/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    public void testFetchAllLikesSuccessfully() throws Exception {
        LikeEntity like1 = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .build();

        LikeEntity like2 = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .build();

        likeRepository.save(like1);
        likeRepository.save(like2);

        mockMvc.perform(get("/api/likes")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @Order(6)
    public void testCheckLikeByUserIdAndMessageId() throws Exception {
        mockMvc.perform(get("/api/likes/checkLike/message")
                        .param("userId", sender.getId().toString())
                        .param("messageId", message.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @Order(7)
    public void testCheckLikeByUserIdAndBookInteractionId() throws Exception {
        mockMvc.perform(get("/api/likes/checkLike/book-interaction")
                        .param("userId", sender.getId().toString())
                        .param("bookInteractionId", interaction.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @Order(8)
    public void testUpdateLikeSuccessfully() throws Exception {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        LikeDTO likeDTO = LikeDTO.builder()
                .userId(sender.getId())
                .bookInteractionId(interaction.getId())
                .build();

        mockMvc.perform(put("/api/likes/update/{id}", savedLike.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedLike.getId()))
                .andExpect(jsonPath("$.userId").value(sender.getId()))
                .andExpect(jsonPath("$.bookInteractionId").value(interaction.getId()));
    }

    @Test
    @Order(9)
    public void testDeleteLikeSuccessfully() throws Exception {
        LikeEntity like = LikeEntity.builder()
                .user(sender)
                .bookInteraction(interaction)
                .build();

        LikeEntity savedLike = likeRepository.save(like);

        mockMvc.perform(delete("/api/likes/delete/{id}", savedLike.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(10)
    public void testDeleteLikeNotFound() throws Exception {
        mockMvc.perform(delete("/api/likes/delete/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    private void initializeMockData() {
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
}