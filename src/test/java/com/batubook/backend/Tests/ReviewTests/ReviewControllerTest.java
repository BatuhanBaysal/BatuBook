package com.batubook.backend.Tests.ReviewTests;

import com.batubook.backend.dto.ReviewDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.ReviewEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.ReviewRepository;
import com.batubook.backend.repository.UserRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ReviewControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private ReviewDTO mockReviewDTO;
    private ReviewEntity mockReviewEntity;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for a single test...");
        userRepository.deleteAll();
        bookRepository.deleteAll();
        reviewRepository.deleteAll();
        initializeMockData();
        logger.info("Test data initialized successfully.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Test completed, no database changes should remain due to @Transactional.");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("All tests completed. Closing resources...");
    }

    @Test
    @Order(1)
    @DisplayName("It should create a review successfully with valid data")
    void createReview_success() throws Exception {
        logger.info("Starting test: It should create a review successfully with valid data.");

        ReviewDTO newReviewDTO = new ReviewDTO();
        newReviewDTO.setReviewText("Test review content from controller.");
        newReviewDTO.setRating(new BigDecimal("4.5"));
        newReviewDTO.setUserId(mockReviewDTO.getUserId());
        newReviewDTO.setBookId(mockReviewDTO.getBookId());

        mockMvc.perform(post("/api/reviews/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newReviewDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewText").value(newReviewDTO.getReviewText()))
                .andExpect(jsonPath("$.rating").value(newReviewDTO.getRating()))
                .andExpect(jsonPath("$.userId").value(newReviewDTO.getUserId()))
                .andExpect(jsonPath("$.bookId").value(newReviewDTO.getBookId()));

        logger.info("Test passed: Review successfully created with text: '{}'", newReviewDTO.getReviewText());
    }

    @Test
    @Order(2)
    @DisplayName("It should return the review when a valid ID is provided")
    void fetchReviewById_success() throws Exception {
        Long reviewId = mockReviewEntity.getId();

        mockMvc.perform(get("/api/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.reviewText").value(mockReviewEntity.getReviewText()))
                .andExpect(jsonPath("$.rating").value(mockReviewEntity.getRating()));
    }

    @Test
    @Order(3)
    @DisplayName("It should return 400 when review is not found by ID")
    void fetchReviewById_notFound() throws Exception {
        mockMvc.perform(get("/api/reviews/{id}", 9999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Review with ID 9999 not found"));
    }

    @Test
    @Order(4)
    @DisplayName("It should return a page of reviews successfully")
    void fetchAllReviews_success() throws Exception {
        mockMvc.perform(get("/api/reviews?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("It should return an empty page when no reviews exist")
    void fetchAllReviews_empty() throws Exception {
        reviewRepository.deleteAll();

        mockMvc.perform(get("/api/reviews?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("It should update a review successfully with valid data")
    void updateReview_success() throws Exception {
        Long reviewId = mockReviewEntity.getId();
        ReviewDTO updatedDTO = new ReviewDTO();
        updatedDTO.setReviewText("Updated review from controller test.");
        updatedDTO.setRating(new BigDecimal("5.0"));
        updatedDTO.setUserId(mockReviewDTO.getUserId());
        updatedDTO.setBookId(mockReviewDTO.getBookId());

        mockMvc.perform(put("/api/reviews/update/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.reviewText").value("Updated review from controller test."))
                .andExpect(jsonPath("$.rating").value("5.0"));
    }

    @Test
    @Order(7)
    @DisplayName("It should return 500 Internal Server Error when updating non-existent review")
    void updateReview_internalServerErrorWhenReviewNotFound() throws Exception {
        ReviewDTO updateDTO = new ReviewDTO();
        updateDTO.setReviewText("Trying to update non-existing review.");
        updateDTO.setRating(new BigDecimal("4.0"));
        updateDTO.setUserId(mockReviewDTO.getUserId());
        updateDTO.setBookId(mockReviewDTO.getBookId());

        mockMvc.perform(put("/api/reviews/update/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Review could not be updated: Review not found with ID: 9999"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    @Order(8)
    @DisplayName("It should delete a review successfully with valid ID")
    void deleteReview_success() throws Exception {
        Long reviewId = mockReviewEntity.getId();

        mockMvc.perform(delete("/api/reviews/delete/{id}", reviewId))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(reviewRepository.findById(reviewId).isPresent());
    }

    @Test
    @Order(9)
    @DisplayName("It should return 404 when deleting non-existent review")
    void deleteReview_notFound() throws Exception {
        mockMvc.perform(delete("/api/reviews/delete/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    private void initializeMockData() {
        UserEntity user = createTestUser();
        BookEntity book = createTestBook();
        mockReviewEntity = createTestReview(user, book);

        mockReviewDTO = new ReviewDTO();
        mockReviewDTO.setReviewText("Review Controller Test!");
        mockReviewDTO.setRating(new BigDecimal("4.5"));
        mockReviewDTO.setUserId(user.getId());
        mockReviewDTO.setBookId(book.getId());
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setUsername("testbatu");
        user.setEmail("testbatu@example.com");
        user.setPassword("securePassword!123");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private BookEntity createTestBook() {
        BookEntity book = new BookEntity();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setIsbn("1234567890");
        book.setPageCount(352);
        book.setPublishDate(LocalDate.of(1949, 6, 8));
        book.setGenre(Genre.DYSTOPIA);

        return bookRepository.save(book);
    }

    private ReviewEntity createTestReview(UserEntity user, BookEntity book) {
        ReviewEntity review = new ReviewEntity();
        review.setReviewText("Review Controller Test!");
        review.setRating(new BigDecimal("4.5"));
        review.setUser(user);
        review.setBook(book);
        return reviewRepository.save(review);
    }
}