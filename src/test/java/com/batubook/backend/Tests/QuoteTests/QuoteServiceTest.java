package com.batubook.backend.Tests.QuoteTests;

import com.batubook.backend.dto.*;
import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.QuoteMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.QuoteRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceImplementation.QuoteServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuoteServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceTest.class);

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteMapper quoteMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    private QuoteEntity sampleQuoteEntity;
    private QuoteDTO sampleQuoteDTO;

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment...");
        reset(quoteRepository, quoteMapper, userRepository, bookRepository);
        initializeMockData();
        logger.info("Test environment setup complete.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up after test...");
        reset(quoteRepository, quoteMapper, userRepository, bookRepository);
        logger.info("Cleanup complete.");
    }

    private void initializeMockData() {
        sampleQuoteEntity = QuoteEntity.builder()
                .id(1L)
                .user(createSampleUserEntity())
                .book(createTestBookEntity())
                .quoteText("This is a test quote")
                .build();

        sampleQuoteDTO = QuoteDTO.builder()
                .id(1L)
                .userId(createSampleUserDTO().getId())
                .bookId(createTestBookDTO().getId())
                .quoteText("This is a test quote")
                .build();
    }

    @Test
    @Order(1)
    void testSaveQuote_Success() {
        logger.info("Running testSaveQuote_Success...");

        UserEntity mockUserEntity = createSampleUserEntity();
        BookEntity mockBookEntity = createTestBookEntity();

        sampleQuoteDTO.setUserId(1L);
        sampleQuoteDTO.setBookId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUserEntity));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBookEntity));
        when(quoteMapper.quoteDTOToQuoteEntity(sampleQuoteDTO)).thenReturn(sampleQuoteEntity);
        when(quoteRepository.save(sampleQuoteEntity)).thenReturn(sampleQuoteEntity);
        when(quoteMapper.quoteEntityToQuoteDTO(sampleQuoteEntity)).thenReturn(sampleQuoteDTO);

        QuoteDTO result = quoteService.registerQuote(sampleQuoteDTO);

        assertNotNull(result);
        assertEquals(sampleQuoteDTO.getQuoteText(), result.getQuoteText());
        assertEquals(sampleQuoteDTO.getUserId(), result.getUserId());
        assertEquals(sampleQuoteDTO.getBookId(), result.getBookId());

        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).findById(1L);
        verify(quoteMapper, times(1)).quoteDTOToQuoteEntity(sampleQuoteDTO);
        verify(quoteRepository, times(1)).save(sampleQuoteEntity);
        verify(quoteMapper, times(1)).quoteEntityToQuoteDTO(sampleQuoteEntity);

        logger.info("testSaveQuote_Success completed successfully.");
    }

    @Test
    @Order(2)
    void testSaveQuote_Fail_NullDTO() {
        logger.info("Running testSaveQuote_Fail_NullDTO...");

        assertThrows(CustomExceptions.InternalServerErrorException.class, () -> quoteService.registerQuote(null));

        verify(quoteRepository, never()).save(any());
        logger.info("testSaveQuote_Fail_NullDTO completed.");
    }

    @Test
    @Order(3)
    void testFindQuoteById_Success() {
        logger.info("Running testFindQuoteById_Success...");

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(sampleQuoteEntity));
        when(quoteMapper.quoteEntityToQuoteDTO(sampleQuoteEntity)).thenReturn(sampleQuoteDTO);

        QuoteDTO result = quoteService.getQuoteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(quoteRepository, times(1)).findById(1L);
        logger.info("testFindQuoteById_Success completed successfully.");
    }

    @Test
    @Order(4)
    void testFindQuoteById_Fail_NotFound() {
        logger.info("Running testFindQuoteById_Fail_NotFound...");

        when(quoteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.NotFoundException.class, () -> quoteService.getQuoteById(99L));
        verify(quoteRepository, times(1)).findById(99L);
        logger.info("testFindQuoteById_Fail_NotFound completed.");
    }

    @Test
    @Order(5)
    void testGetAllQuotes_Success() {
        logger.info("Running testGetAllQuotes_Success...");
        List<QuoteEntity> quotes = Collections.singletonList(sampleQuoteEntity);
        Pageable pageable = PageRequest.of(0, 10);
        Page<QuoteEntity> page = new PageImpl<>(quotes, pageable, quotes.size());

        when(quoteRepository.findAll(pageable)).thenReturn(page);
        when(quoteMapper.quoteEntityToQuoteDTO(any(QuoteEntity.class))).thenReturn(sampleQuoteDTO);

        Page<QuoteDTO> result = quoteService.getAllQuotes(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleQuoteDTO.getId(), result.getContent().get(0).getId());

        verify(quoteRepository, times(1)).findAll(pageable);
        verify(quoteMapper, times(1)).quoteEntityToQuoteDTO(any(QuoteEntity.class));

        logger.info("testGetAllQuotes_Success completed successfully.");
    }

    @Test
    @Order(6)
    void testUpdateQuote_Success() {
        logger.info("Running testUpdateQuote_Success...");

        sampleQuoteDTO.setQuoteText("Updated quote text");
        sampleQuoteEntity.setQuoteText("Updated quote text");

        when(quoteRepository.findById(1L)).thenReturn(Optional.of(sampleQuoteEntity));
        when(quoteRepository.save(any(QuoteEntity.class))).thenReturn(sampleQuoteEntity);
        when(quoteMapper.quoteEntityToQuoteDTO(any(QuoteEntity.class))).thenReturn(sampleQuoteDTO);

        QuoteDTO result = quoteService.modifyQuote(1L, sampleQuoteDTO);

        assertNotNull(result);
        assertEquals("Updated quote text", result.getQuoteText());

        verify(quoteRepository, times(1)).save(any(QuoteEntity.class));
        logger.info("testUpdateQuote_Success completed successfully.");
    }

    @Test
    @Order(7)
    void testUpdateQuote_Fail_NotFound() {
        logger.info("Running testUpdateQuote_Fail_NotFound...");

        when(quoteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.InternalServerErrorException.class, () -> quoteService.modifyQuote(99L, sampleQuoteDTO));
        verify(quoteRepository, never()).save(any());
        logger.info("testUpdateQuote_Fail_NotFound completed.");
    }

    @Test
    @Order(8)
    void testDeleteQuote_Success() {
        logger.info("Running testDeleteQuote_Success...");

        when(quoteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(quoteRepository).deleteById(1L);

        assertDoesNotThrow(() -> quoteService.removeQuote(1L));
        verify(quoteRepository, times(1)).deleteById(1L);
        logger.info("testDeleteQuote_Success completed successfully.");
    }

    @Test
    @Order(9)
    void testDeleteQuote_Fail_NotFound() {
        logger.info("Running testDeleteQuote_Fail_NotFound...");

        when(quoteRepository.existsById(99L)).thenReturn(false);

        assertThrows(CustomExceptions.NotFoundException.class, () -> quoteService.removeQuote(99L));
        verify(quoteRepository, never()).deleteById(99L);
        logger.info("testDeleteQuote_Fail_NotFound completed.");
    }

    private UserEntity createSampleUserEntity() {
        UserProfileEntity userProfile = createSampleUserProfileEntity(null);
        return UserEntity.builder()
                .id(1L)
                .username("bbatuhan")
                .email("bbatuhan@batubook.com")
                .password("validPassword!123")
                .role(Role.ADMIN)
                .userProfile(userProfile)
                .build();
    }

    private UserProfileEntity createSampleUserProfileEntity(UserEntity user) {
        return UserProfileEntity.builder()
                .user(user)
                .biography("Full Stack Software Developer")
                .location("Istanbul")
                .dateOfBirth(LocalDate.of(2000, 8, 14))
                .gender(Gender.MALE)
                .profileImageUrl("https://batubook.com/profile.jpg")
                .education("Computer Engineering")
                .occupation("Software Developer")
                .interests("Coding, Reading, Music")
                .build();
    }

    private UserDTO createSampleUserDTO() {
        UserProfileDTO userProfileDTO = createSampleUserProfileDTO();
        return UserDTO.builder()
                .id(1L)
                .username("bbatuhan")
                .email("bbatuhan@batubook.com")
                .password("validPassword!123")
                .role(Role.ADMIN)
                .userProfile(userProfileDTO)
                .build();
    }

    private UserProfileDTO createSampleUserProfileDTO() {
        return UserProfileDTO.builder()
                .biography("Full Stack Software Developer")
                .location("Istanbul")
                .dateOfBirth(String.valueOf(LocalDate.of(2000, 8, 14)))
                .gender(Gender.MALE)
                .profileImageUrl("https://batubook.com/profile.jpg")
                .education("Computer Engineering")
                .occupation("Software Developer")
                .interests("Coding, Reading, Music")
                .build();
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