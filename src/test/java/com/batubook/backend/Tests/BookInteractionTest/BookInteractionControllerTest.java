package com.batubook.backend.Tests.BookInteractionTest;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.dto.BookInteractionDTO;
import com.batubook.backend.dto.UserDTO;
import com.batubook.backend.dto.UserProfileDTO;
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
public class BookInteractionControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(BookInteractionControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookInteractionRepository bookInteractionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    private BookInteractionDTO mockInteractionDTO;
    private BookInteractionEntity mockInteractionEntity;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all BookInteractionController tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for BookInteractionController...");
        bookRepository.deleteAll();
        userRepository.deleteAll();
        bookInteractionRepository.deleteAll();
        initializeMockData();
        logger.info("Book Interaction test data initialized.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Book Interaction test completed.");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("All BookInteractionController tests completed.");
    }

    @Test
    @Order(1)
    @DisplayName("It should create a book interaction successfully with a new user and new book")
    void createBookInteraction_success() throws Exception {
        logger.info("Starting test: createBookInteraction_success");
        BookEntity newBookEntity = new BookEntity();
        newBookEntity.setTitle("Brave New World");
        newBookEntity.setAuthor("Aldous Huxley");
        newBookEntity.setIsbn("1234567892");
        newBookEntity.setPageCount(311);
        newBookEntity.setPublishDate(LocalDate.parse("1932-08-01"));
        newBookEntity.setGenre(Genre.DYSTOPIA);

        BookEntity savedBook = bookRepository.save(newBookEntity);

        UserEntity newUserEntity = new UserEntity();
        newUserEntity.setUsername("newUser123");
        newUserEntity.setEmail("newuser123@batubook.com");
        newUserEntity.setPassword("SecurePassword!123");
        newUserEntity.setRole(Role.USER);

        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setDateOfBirth(LocalDate.of(1990, 5, 15));
        userProfile.setBiography("New user biography");
        userProfile.setLocation("Istanbul");
        userProfile.setOccupation("Software Developer");
        userProfile.setEducation("Computer Science");
        userProfile.setInterests("Technology, Reading");
        userProfile.setProfileImageUrl("http://example.com/profile.jpg");
        userProfile.setGender(Gender.MALE);

        newUserEntity.setUserProfile(userProfile);
        userProfile.setUser(newUserEntity);

        UserEntity savedUser = userRepository.save(newUserEntity);

        BookInteractionDTO newInteractionDTO = new BookInteractionDTO();
        newInteractionDTO.setDescription("A thought-provoking novel!");
        newInteractionDTO.setIsRead(true);
        newInteractionDTO.setIsLiked(true);
        newInteractionDTO.setBookId(savedBook.getId());
        newInteractionDTO.setUserId(savedUser.getId());

        mockMvc.perform(post("/api/book-interactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newInteractionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("A thought-provoking novel!"))
                .andExpect(jsonPath("$.isRead").value(true))
                .andExpect(jsonPath("$.isLiked").value(true))
                .andExpect(jsonPath("$.bookId").value(savedBook.getId()))
                .andExpect(jsonPath("$.userId").value(savedUser.getId()));

        logger.info("Successfully created book interaction for userId: {} and bookId: {}",
                newInteractionDTO.getUserId(), newInteractionDTO.getBookId());
    }

    @Test
    @Order(2)
    @DisplayName("It should fetch a book interaction by ID successfully")
    void fetchBookInteractionById_success() throws Exception {
        logger.info("Starting test: fetchBookInteractionById_success");

        mockMvc.perform(get("/api/book-interactions/{id}", mockInteractionEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockInteractionEntity.getId()))
                .andExpect(jsonPath("$.description").value(mockInteractionEntity.getDescription()))
                .andExpect(jsonPath("$.isRead").value(mockInteractionEntity.getIsRead()))
                .andExpect(jsonPath("$.isLiked").value(mockInteractionEntity.getIsLiked()))
                .andExpect(jsonPath("$.bookId").value(mockInteractionEntity.getBook().getId()))
                .andExpect(jsonPath("$.userId").value(mockInteractionEntity.getUser().getId()));

        logger.info("Book interaction fetched successfully with ID: {}", mockInteractionEntity.getId());
    }

    @Test
    @Order(3)
    @DisplayName("It should return 404 when book interaction not found by ID")
    void fetchBookInteractionById_notFound() throws Exception {
        logger.info("Starting test: fetchBookInteractionById_notFound");

        mockMvc.perform(get("/api/book-interactions/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Book interaction not found as expected.");
    }

    @Test
    @Order(4)
    @DisplayName("It should fetch all book interactions with pagination")
    void fetchAllBookInteractions_success() throws Exception {
        logger.info("Starting test: fetchAllBookInteractions_success");

        mockMvc.perform(get("/api/book-interactions?page=0&size=5&sort=id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))  // Assuming 1 mock interaction exists
                .andExpect(jsonPath("$.content[0].description").value(mockInteractionEntity.getDescription()));

        logger.info("Book interactions fetched with pagination.");
    }

    @Test
    @Order(5)
    @DisplayName("It should update a book interaction successfully")
    void updateBookInteraction_success() throws Exception {
        logger.info("Starting test: updateBookInteraction_success");

        mockInteractionDTO.setDescription("Updated interaction description");
        mockInteractionDTO.setIsRead(true);
        mockInteractionDTO.setIsLiked(false);

        mockMvc.perform(put("/api/book-interactions/update/{id}", mockInteractionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockInteractionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated interaction description"));

        logger.info("Successfully updated book interaction.");
    }

    @Test
    @Order(6)
    @DisplayName("It should return 500 when updating non-existent book interaction")
    void updateBookInteraction_notFound() throws Exception {
        logger.info("Starting test: updateBookInteraction_notFound");

        mockMvc.perform(put("/api/book-interactions/update/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockInteractionDTO)))
                .andExpect(status().isInternalServerError());

        logger.info("Book interaction update failed as expected.");
    }

    @Test
    @Order(7)
    @DisplayName("It should delete a book interaction successfully")
    void deleteBookInteraction_success() throws Exception {
        logger.info("Starting test: deleteBookInteraction_success");

        mockMvc.perform(delete("/api/book-interactions/delete/{id}", mockInteractionEntity.getId()))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(bookInteractionRepository.existsById(mockInteractionEntity.getId()));

        logger.info("Successfully deleted book interaction.");
    }

    @Test
    @Order(8)
    @DisplayName("It should return 404 when deleting non-existent book interaction")
    void deleteBookInteraction_notFound() throws Exception {
        logger.info("Starting test: deleteBookInteraction_notFound");

        mockMvc.perform(delete("/api/book-interactions/delete/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Book interaction deletion failed as expected.");
    }


    public void initializeMockData() {
        BookEntity mockBookEntity = createMockBook();
        UserEntity mockUserEntity = createMockUser();

        mockInteractionDTO = new BookInteractionDTO();
        mockInteractionDTO.setDescription("Good Book!");
        mockInteractionDTO.setIsRead(true);
        mockInteractionDTO.setIsLiked(true);
        mockInteractionDTO.setBookId(mockBookEntity.getId());
        mockInteractionDTO.setUserId(mockUserEntity.getId());

        BookInteractionEntity interaction = new BookInteractionEntity();
        interaction.setDescription(mockInteractionDTO.getDescription());
        interaction.setIsRead(mockInteractionDTO.getIsRead());
        interaction.setIsLiked(mockInteractionDTO.getIsLiked());
        interaction.setBook(mockBookEntity);
        interaction.setUser(mockUserEntity);

        mockInteractionEntity = bookInteractionRepository.save(interaction);
    }

    private BookEntity createMockBook() {
        BookDTO mockBookDTO = new BookDTO();
        mockBookDTO.setTitle("1984");
        mockBookDTO.setAuthor("George Orwell");
        mockBookDTO.setIsbn("1111111111");
        mockBookDTO.setPageCount(352);
        mockBookDTO.setPublishDate("1949-06-08");
        mockBookDTO.setGenre(Genre.DYSTOPIA);

        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(mockBookDTO.getTitle());
        bookEntity.setAuthor(mockBookDTO.getAuthor());
        bookEntity.setIsbn(mockBookDTO.getIsbn());
        bookEntity.setPageCount(mockBookDTO.getPageCount());
        bookEntity.setPublishDate(LocalDate.parse(mockBookDTO.getPublishDate()));
        bookEntity.setGenre(mockBookDTO.getGenre());

        return bookRepository.save(bookEntity);
    }

    private UserEntity createMockUser() {
        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setUsername("testbatubook");
        mockUserDTO.setEmail("testbatubook@batubook.com");
        mockUserDTO.setPassword("validPassword!123");
        mockUserDTO.setRole(Role.USER);

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setDateOfBirth("2000-08-14");
        profileDTO.setBiography("Software developer");
        profileDTO.setLocation("Istanbul");
        profileDTO.setOccupation("Engineer");
        profileDTO.setEducation("University");
        profileDTO.setInterests("Coding, Reading");
        profileDTO.setProfileImageUrl("http://image.url/profile.jpg");
        profileDTO.setGender(Gender.MALE);

        mockUserDTO.setUserProfile(profileDTO);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(mockUserDTO.getUsername());
        userEntity.setEmail(mockUserDTO.getEmail());
        userEntity.setPassword("validPassword!123");
        userEntity.setRole(Role.USER);

        UserProfileEntity profileEntity = new UserProfileEntity();
        profileEntity.setDateOfBirth(LocalDate.of(2000, 8, 14));
        profileEntity.setBiography("Software developer");
        profileEntity.setLocation("Istanbul");
        profileEntity.setOccupation("Engineer");
        profileEntity.setEducation("University");
        profileEntity.setInterests("Coding, Reading");
        profileEntity.setProfileImageUrl("http://image.url/profile.jpg");
        profileEntity.setGender(Gender.MALE);

        userEntity.setUserProfile(profileEntity);
        profileEntity.setUser(userEntity);

        return userRepository.save(userEntity);
    }
}