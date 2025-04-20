package com.batubook.backend.Tests.BookTests;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.BookMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.service.serviceImplementation.BookServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceTest.class);

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment...");
        reset(bookRepository, bookMapper);
        logger.info("Test environment setup complete.");
    }

    @Test
    @Order(1)
    @DisplayName("Should create book successfully")
    void shouldCreateBookSuccessfully() {
        logger.info("Starting test for creating book...");
        BookDTO bookDTO = createTestBookDTO();
        BookEntity bookEntity = createTestBookEntity();

        when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);
        when(bookMapper.bookDTOToEntity(bookDTO)).thenReturn(bookEntity);
        when(bookMapper.bookEntityToDTO(bookEntity)).thenReturn(bookDTO);

        BookDTO result = bookService.registerBook(bookDTO);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(bookDTO.getTitle(), result.getTitle()),
                () -> assertEquals(bookDTO.getAuthor(), result.getAuthor())
        );

        verify(bookRepository).save(any(BookEntity.class));
        verify(bookMapper).bookDTOToEntity(bookDTO);
        verify(bookMapper).bookEntityToDTO(bookEntity);
        logger.info("Test for creating book completed.");
    }

    @Test
    @Order(2)
    @DisplayName("Should fail to create book when title is missing")
    void shouldFailToCreateBookWhenTitleIsMissing() {
        logger.info("Starting test for failing to create book when title is missing...");
        BookDTO bookDTO = createTestBookDTO();
        bookDTO.setTitle("");

        when(bookMapper.bookDTOToEntity(bookDTO)).thenThrow(new CustomExceptions.InternalServerErrorException("Title cannot be empty"));
        CustomExceptions.InternalServerErrorException exception = assertThrows(CustomExceptions.InternalServerErrorException.class, () -> bookService.registerBook(bookDTO));
        assertEquals("Book could not be created: Title cannot be empty", exception.getMessage());

        verify(bookRepository, times(0)).save(any(BookEntity.class));
        logger.info("Test for failing to create book when title is missing completed.");
    }

    @Test
    @Order(3)
    @DisplayName("Should successfully retrieve book by ID")
    void shouldSuccessfullyRetrieveBookById() {
        logger.info("Starting test for retrieving book by ID...");
        Long bookId = 1L;
        BookEntity bookEntity = createTestBookEntity();
        BookDTO expectedBookDTO = createTestBookDTO();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.bookEntityToDTO(bookEntity)).thenReturn(expectedBookDTO);

        BookDTO result = bookService.getBookById(bookId);
        assertNotNull(result);
        assertEquals(expectedBookDTO.getTitle(), result.getTitle());

        verify(bookRepository).findById(bookId);
        verify(bookMapper).bookEntityToDTO(bookEntity);
        logger.info("Test for retrieving book by ID completed.");
    }

    @Test
    @Order(4)
    @DisplayName("Should throw exception when book not found by ID")
    void shouldThrowExceptionWhenBookNotFoundById() {
        logger.info("Starting test for retrieving book by ID (book not found)...");
        Long bookId = 999L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        CustomExceptions.NotFoundException exception = assertThrows(CustomExceptions.NotFoundException.class, () -> bookService.getBookById(bookId));
        assertEquals("Book not found with ID: 999", exception.getMessage());

        verify(bookRepository).findById(bookId);
        logger.info("Test for retrieving book by ID (book not found) completed.");
    }

    @Test
    @Order(5)
    @DisplayName("Should successfully update book details")
    void shouldSuccessfullyUpdateBookDetails() {
        logger.info("Starting test for successfully updating book details...");
        Long bookId = 1L;
        BookDTO bookDTO = createTestBookDTO();
        BookEntity existingBook = createTestBookEntity();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookMapper.bookDTOToEntity(bookDTO)).thenReturn(existingBook);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.bookEntityToDTO(existingBook)).thenReturn(bookDTO);

        BookDTO result = bookService.modifyBook(bookId, bookDTO);

        assertNotNull(result);
        assertEquals(bookDTO.getTitle(), result.getTitle());
        assertEquals(bookDTO.getAuthor(), result.getAuthor());

        verify(bookRepository).findById(bookId);
        verify(bookRepository).save(existingBook);
        verify(bookMapper).bookEntityToDTO(existingBook);
        logger.info("Test for successfully updating book details completed.");
    }

    @Test
    @Order(6)
    @DisplayName("Should fail to update book when book not found")
    void shouldFailToUpdateBookWhenBookNotFound() {
        logger.info("Starting test for failing to update book when book is not found...");
        Long bookId = 999L;
        BookDTO bookDTO = createTestBookDTO();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        CustomExceptions.InternalServerErrorException exception = assertThrows(CustomExceptions.InternalServerErrorException.class, () -> bookService.modifyBook(bookId, bookDTO));
        assertEquals("Book could not be updated: Book not found with id: 999", exception.getMessage());

        verify(bookRepository).findById(bookId);
        logger.info("Test for failing to update book when book is not found completed.");
    }

    @Test
    @Order(7)
    @DisplayName("Should successfully remove book by ID")
    void shouldSuccessfullyRemoveBookById() {
        logger.info("Starting test for successfully removing book by ID...");
        Long bookId = 1L;

        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(bookId);
        bookService.removeBook(bookId);

        verify(bookRepository).deleteById(bookId);
        verify(bookRepository).existsById(bookId);
        logger.info("Test for successfully removing book by ID completed.");
    }

    @Test
    @Order(8)
    @DisplayName("Should throw exception when book not found for removal")
    void shouldThrowExceptionWhenBookNotFoundForRemove() {
        logger.info("Starting test for failing to remove book when book not found...");
        Long bookId = 999L;

        when(bookRepository.existsById(bookId)).thenReturn(false);
        CustomExceptions.NotFoundException exception = assertThrows(CustomExceptions.NotFoundException.class, () -> bookService.removeBook(bookId));
        assertEquals("Book not found with ID: 999", exception.getMessage());

        verify(bookRepository, times(0)).deleteById(bookId);
        verify(bookRepository).existsById(bookId);
        logger.info("Test for failing to remove book when book not found completed.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up after test...");
        reset(bookRepository, bookMapper);
        logger.info("Cleanup complete.");
    }

    private BookEntity createTestBookEntity() {
        return BookEntity.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("1234567890")
                .pageCount(352)
                .publishDate(LocalDate.of(1949, 6, 8))
                .genre(Genre.DYSTOPIA)
                .bookCoverImageUrl("https://tr.wikipedia.org/wiki/Dosya:1984.jpg")
                .summary("More broadly, the novel examines the role of truth and facts within societies and the ways in which they can be manipulated.")
                .build();
    }

    private BookDTO createTestBookDTO() {
        return BookDTO.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("1234567890")
                .pageCount(352)
                .publishDate(String.valueOf(LocalDate.of(1949, 6, 8)))
                .genre(Genre.DYSTOPIA)
                .bookCoverImageUrl("https://tr.wikipedia.org/wiki/Dosya:1984.jpg")
                .summary("More broadly, the novel examines the role of truth and facts within societies and the ways in which they can be manipulated.")
                .build();
    }
}