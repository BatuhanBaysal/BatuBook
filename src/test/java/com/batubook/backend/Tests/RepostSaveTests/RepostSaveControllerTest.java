package com.batubook.backend.Tests.RepostSaveTests;

import com.batubook.backend.dto.RepostSaveDTO;
import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.ActionType;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepostSaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        user = userRepository.save(UserEntity.builder()
                .username("testUser")
                .email("test@example.com")
                .password("Test1234!")
                .role(Role.USER)
                .build());

        BookEntity book = bookRepository.save(BookEntity.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9876543210")
                .genre(Genre.SCIENCE_FICTION)
                .publishDate(LocalDate.now().minusYears(1))
                .pageCount(300)
                .summary("Test summary")
                .build());

        review = reviewRepository.save(ReviewEntity.builder()
                .reviewText("Very insightful")
                .rating(BigDecimal.valueOf(4.0))
                .user(user)
                .book(book)
                .build());

        quote = quoteRepository.save(QuoteEntity.builder()
                .quoteText("Important quote")
                .user(user)
                .book(book)
                .build());

        bookInteraction = bookInteractionRepository.save(BookInteractionEntity.builder()
                .user(user)
                .book(book)
                .isRead(true)
                .isLiked(true)
                .description("Great interaction")
                .build());
    }

    @Test
    @Order(1)
    public void testCreateRepostSaveWithReview() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .userId(user.getId())
                .reviewId(review.getId())
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.reviewId").value(review.getId()))
                .andExpect(jsonPath("$.actionType").value("repost"));
    }

    @Test
    @Order(2)
    public void testCreateRepostSaveWithQuote() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .userId(user.getId())
                .quoteId(quote.getId())
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.quoteId").value(quote.getId()))
                .andExpect(jsonPath("$.actionType").value("repost"));
    }

    @Test
    @Order(3)
    public void testCreateRepostSaveWithBookInteraction() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .userId(user.getId())
                .bookInteractionId(bookInteraction.getId())
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.bookInteractionId").value(bookInteraction.getId()))
                .andExpect(jsonPath("$.actionType").value("repost"));
    }

    @Test
    @Order(4)
    public void testCreateRepostSaveWithInvalidData() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Repost-save could not be created: Only one content type (Review, Quote, or BookInteraction) can be referenced."))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    @Order(5)
    public void testGetRepostSaveById() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .userId(user.getId())
                .reviewId(review.getId())
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.reviewId").value(review.getId()))
                .andExpect(jsonPath("$.actionType").value("repost"))
                .andDo(result -> {
                    String repostSaveId = result.getResponse().getContentAsString().split(":")[1].split(",")[0];
                    mockMvc.perform(get("/api/repost-saves/{id}", repostSaveId))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.userId").value(user.getId()))
                            .andExpect(jsonPath("$.reviewId").value(review.getId()))
                            .andExpect(jsonPath("$.actionType").value("repost"));
                });
    }

    @Test
    @Order(6)
    public void testGetRepostSaveByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/repost-saves/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    public void testGetAllRepostSaves() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .userId(user.getId())
                .reviewId(review.getId())
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/repost-saves")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @Order(8)
    public void testGetAllRepostSavesEmpty() throws Exception {
        repostSaveRepository.deleteAll();

        mockMvc.perform(get("/api/repost-saves")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @Order(9)
    public void testDeleteRepostSave() throws Exception {
        RepostSaveDTO repostSaveDTO = RepostSaveDTO.builder()
                .userId(user.getId())
                .reviewId(review.getId())
                .actionType(ActionType.REPOST)
                .build();

        mockMvc.perform(post("/api/repost-saves/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repostSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.reviewId").value(review.getId()))
                .andExpect(jsonPath("$.actionType").value("repost"))
                .andDo(result -> {
                    String repostSaveId = result.getResponse().getContentAsString().split(":")[1].split(",")[0];
                    mockMvc.perform(delete("/api/repost-saves/delete/{id}", repostSaveId))
                            .andExpect(status().isNoContent());
                });
    }

    @Test
    @Order(10)
    public void testDeleteRepostSaveNotFound() throws Exception {
        mockMvc.perform(delete("/api/repost-saves/delete/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}