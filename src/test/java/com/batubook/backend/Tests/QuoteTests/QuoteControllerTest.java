package com.batubook.backend.Tests.QuoteTests;

import com.batubook.backend.dto.QuoteDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.QuoteEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.UserProfileEntity;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.QuoteRepository;
import com.batubook.backend.repository.UserProfileRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuoteControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private QuoteDTO mockQuoteDTO;
    private QuoteEntity mockQuoteEntity;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for a single test...");
        userRepository.deleteAll();
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
    @DisplayName("It should create a quote successfully with valid data")
    void createQuote_success() throws Exception {
        logger.info("Starting test: It should create a quote successfully with valid data.");

        QuoteDTO newQuoteDTO = new QuoteDTO();
        newQuoteDTO.setQuoteText("Test quote content from controller.");
        newQuoteDTO.setUserId(mockQuoteDTO.getUserId());
        newQuoteDTO.setBookId(mockQuoteDTO.getBookId());

        mockMvc.perform(post("/api/quotes/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newQuoteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteText").value(newQuoteDTO.getQuoteText()))
                .andExpect(jsonPath("$.userId").value(newQuoteDTO.getUserId()))
                .andExpect(jsonPath("$.bookId").value(newQuoteDTO.getBookId()));

        logger.info("Test passed: Quote successfully created with text: '{}'", newQuoteDTO.getQuoteText());
    }

    @Test
    @Order(2)
    @DisplayName("It should return the quote when a valid ID is provided")
    void fetchQuoteById_success() throws Exception {
        Long quoteId = mockQuoteEntity.getId();

        mockMvc.perform(get("/api/quotes/{id}", quoteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quoteId))
                .andExpect(jsonPath("$.quoteText").value(mockQuoteEntity.getQuoteText()));
    }

    @Test
    @Order(3)
    @DisplayName("It should return 404 when quote is not found by ID")
    void fetchQuoteById_notFound() throws Exception {
        mockMvc.perform(get("/api/quotes/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("It should return a page of quotes successfully")
    void fetchAllQuotes_success() throws Exception {
        mockMvc.perform(get("/api/quotes?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("It should return an empty page when no quotes exist")
    void fetchAllQuotes_empty() throws Exception {
        quoteRepository.deleteAll();

        mockMvc.perform(get("/api/quotes?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("It should update a quote successfully with valid data")
    void updateQuote_success() throws Exception {
        Long quoteId = mockQuoteEntity.getId();
        QuoteDTO updatedDTO = new QuoteDTO();
        updatedDTO.setQuoteText("Updated quote from controller test.");
        updatedDTO.setUserId(mockQuoteDTO.getUserId());
        updatedDTO.setBookId(mockQuoteDTO.getBookId());

        mockMvc.perform(put("/api/quotes/update/{id}", quoteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quoteId))
                .andExpect(jsonPath("$.quoteText").value("Updated quote from controller test."));
    }

    @Test
    @Order(7)
    @DisplayName("It should return 500 Internal Server Error when updating non-existent quote")
    void updateQuote_internalServerErrorWhenQuoteNotFound() throws Exception {
        QuoteDTO updateDTO = new QuoteDTO();
        updateDTO.setQuoteText("Trying to update non-existing quote.");
        updateDTO.setUserId(mockQuoteDTO.getUserId());
        updateDTO.setBookId(mockQuoteDTO.getBookId());

        mockMvc.perform(put("/api/quotes/update/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Quote could not be updated: Quote not found"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    @Order(8)
    @DisplayName("It should delete a quote successfully with valid ID")
    void deleteQuote_success() throws Exception {
        Long quoteId = mockQuoteEntity.getId();

        mockMvc.perform(delete("/api/quotes/delete/{id}", quoteId))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(quoteRepository.findById(quoteId).isPresent());
    }

    @Test
    @Order(9)
    @DisplayName("It should return 404 when deleting non-existent quote")
    void deleteQuote_notFound() throws Exception {
        mockMvc.perform(delete("/api/quotes/delete/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    private void initializeMockData() {
        UserEntity user = createTestUser();
        BookEntity book = createTestBook();
        mockQuoteEntity = createTestQuote(user, book);

        mockQuoteDTO = new QuoteDTO();
        mockQuoteDTO.setQuoteText("Quote Controller Test!");
        mockQuoteDTO.setUserId(user.getId());
        mockQuoteDTO.setBookId(book.getId());
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setUsername("testbatu");
        user.setEmail("testbatu@example.com");
        user.setPassword("securePassword!123");
        user.setRole(Role.USER);

        UserProfileEntity profile = new UserProfileEntity();
        profile.setDateOfBirth(LocalDate.of(1990, 1, 1));
        profile.setBiography("Software developer");
        profile.setLocation("Istanbul");
        profile.setOccupation("Engineer");
        profile.setEducation("University");
        profile.setInterests("Coding, Reading");
        profile.setProfileImageUrl("http://image.url/profile.jpg");
        profile.setGender(Gender.MALE);
        profile.setUser(user);

        user.setUserProfile(profile);

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

    private QuoteEntity createTestQuote(UserEntity user, BookEntity book) {
        QuoteEntity quote = new QuoteEntity();
        quote.setQuoteText("Quote Controller Test!");
        quote.setUser(user);
        quote.setBook(book);
        return quoteRepository.save(quote);
    }
}