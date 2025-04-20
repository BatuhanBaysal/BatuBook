package com.batubook.backend.Tests.BookTests;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.repository.BookRepository;
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
public class BookControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(BookControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private BookDTO mockBookDTO;
    private BookEntity mockBookEntity;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all BookController tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for BookController...");
        bookRepository.deleteAll();
        initializeMockData();
        logger.info("Book test data initialized.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Book test completed.");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("All BookController tests completed.");
    }

    @Test
    @Order(1)
    @DisplayName("It should create a book successfully with valid data")
    void createBook_success() throws Exception {
        logger.info("Starting test: createBook_success");

        BookDTO newBook = new BookDTO();
        newBook.setTitle("Brave New World");
        newBook.setAuthor("Aldous Huxley");
        newBook.setIsbn("9876543210");
        newBook.setPageCount(288);
        newBook.setPublishDate("1932-01-01");
        newBook.setGenre(Genre.DYSTOPIA);

        mockMvc.perform(post("/api/books/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(newBook.getTitle()))
                .andExpect(jsonPath("$.author").value(newBook.getAuthor()));

        logger.info("Book created successfully with title: {}", newBook.getTitle());
    }

    @Test
    @Order(2)
    @DisplayName("It should fetch a book by ID successfully")
    void fetchBookById_success() throws Exception {
        logger.info("Starting test: fetchBookById_success");

        mockMvc.perform(get("/api/books/{id}", mockBookEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBookEntity.getId()))
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.author").value("George Orwell"));

        logger.info("Book fetched successfully with ID: {}", mockBookEntity.getId());
    }

    @Test
    @Order(3)
    @DisplayName("It should return 404 when book not found by ID")
    void fetchBookById_notFound() throws Exception {
        logger.info("Starting test: fetchBookById_notFound");

        mockMvc.perform(get("/api/books/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Book not found as expected.");
    }

    @Test
    @Order(4)
    @DisplayName("It should fetch all books with pagination")
    void fetchAllBooks_success() throws Exception {
        logger.info("Starting test: fetchAllBooks_success");

        mockMvc.perform(get("/api/books?page=0&size=5&sort=id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value(mockBookEntity.getTitle()));

        logger.info("Books fetched with pagination.");
    }

    @Test
    @Order(5)
    @DisplayName("It should update a book successfully")
    void updateBook_success() throws Exception {
        logger.info("Starting test: updateBook_success");

        mockBookDTO.setTitle("Animal Farm");
        mockBookDTO.setAuthor("George Orwell");
        mockBookDTO.setPageCount(112);
        mockBookDTO.setIsbn("1122334455");
        mockBookDTO.setPublishDate("1945-08-17");
        mockBookDTO.setGenre(Genre.DYSTOPIA);

        mockMvc.perform(put("/api/books/update/{id}", mockBookEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Animal Farm"));

        logger.info("Book updated successfully.");
    }

    @Test
    @Order(6)
    @DisplayName("It should return 500 when updating non-existent book")
    void updateBook_notFound() throws Exception {
        logger.info("Starting test: updateBook_notFound");

        mockMvc.perform(put("/api/books/update/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mockBookDTO)))
                .andExpect(status().isInternalServerError());

        logger.info("Book update failed as expected (internal server error).");
    }

    @Test
    @Order(7)
    @DisplayName("It should delete a book successfully")
    void deleteBook_success() throws Exception {
        logger.info("Starting test: deleteBook_success");

        mockMvc.perform(delete("/api/books/delete/{id}", mockBookEntity.getId()))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(bookRepository.existsById(mockBookEntity.getId()));

        logger.info("Book deleted successfully.");
    }

    @Test
    @Order(8)
    @DisplayName("It should return 404 when deleting non-existent book")
    void deleteBook_notFound() throws Exception {
        logger.info("Starting test: deleteBook_notFound");

        mockMvc.perform(delete("/api/books/delete/{id}", 999L))
                .andExpect(status().isNotFound());

        logger.info("Book deletion failed as expected (not found).");
    }

    private void initializeMockData() {
        mockBookDTO = new BookDTO();
        mockBookDTO.setTitle("1984");
        mockBookDTO.setAuthor("George Orwell");
        mockBookDTO.setIsbn("1234567890");
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

        mockBookEntity = bookRepository.save(bookEntity);
    }
}