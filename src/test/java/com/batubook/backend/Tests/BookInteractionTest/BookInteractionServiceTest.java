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
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.BookInteractionMapper;
import com.batubook.backend.repository.BookInteractionRepository;
import com.batubook.backend.service.serviceImplementation.BookInteractionServiceImpl;
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
public class BookInteractionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BookInteractionServiceTest.class);

    @Mock
    private BookInteractionRepository bookInteractionRepository;

    @Mock
    private BookInteractionMapper bookInteractionMapper;

    @InjectMocks
    private BookInteractionServiceImpl bookInteractionService;

    private BookInteractionEntity sampleInteractionEntity;
    private BookInteractionDTO sampleInteractionDTO;

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment...");
        reset(bookInteractionRepository, bookInteractionMapper);
        initializeMockData();
        logger.info("Test environment setup complete.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up after test...");
        reset(bookInteractionRepository, bookInteractionMapper);
        logger.info("Cleanup complete.");
    }

    private void initializeMockData() {
        sampleInteractionEntity = BookInteractionEntity.builder()
                .id(1L)
                .user(createSampleUserEntity())
                .book(createTestBookEntity())
                .description("TEST")
                .isRead(true)
                .isLiked(false)
                .build();

        sampleInteractionDTO = BookInteractionDTO.builder()
                .id(1L)
                .userId(createSampleUserDTO().getId())
                .bookId(createTestBookDTO().getId())
                .description("TEST")
                .isRead(true)
                .isLiked(false)
                .build();
    }

    @Test
    @Order(1)
    void testSaveInteraction_Success() {
        logger.info("Running testSaveInteraction_Success...");

        when(bookInteractionMapper.bookInteractionDTOToEntity(any())).thenReturn(sampleInteractionEntity);
        when(bookInteractionRepository.save(any())).thenReturn(sampleInteractionEntity);
        when(bookInteractionMapper.bookInteractionEntityToDTO(any())).thenReturn(sampleInteractionDTO);

        BookInteractionDTO result = bookInteractionService.registerBookInteraction(sampleInteractionDTO);

        assertNotNull(result);
        assertEquals(sampleInteractionDTO.getId(), result.getId());
        verify(bookInteractionRepository, times(1)).save(any());
        logger.info("testSaveInteraction_Success completed successfully.");
    }

    @Test
    @Order(2)
    void testSaveInteraction_Fail_NullDTO() {
        logger.info("Running testSaveInteraction_Fail_NullDTO...");

        assertThrows(CustomExceptions.InternalServerErrorException.class, () -> bookInteractionService.registerBookInteraction(null));

        verify(bookInteractionRepository, never()).save(any());
        logger.info("testSaveInteraction_Fail_NullDTO completed.");
    }

    @Test
    @Order(3)
    void testFindById_Success() {
        logger.info("Running testFindById_Success...");

        when(bookInteractionRepository.findById(1L)).thenReturn(Optional.of(sampleInteractionEntity));
        when(bookInteractionMapper.bookInteractionEntityToDTO(sampleInteractionEntity)).thenReturn(sampleInteractionDTO);

        BookInteractionDTO result = bookInteractionService.getBookInteractionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookInteractionRepository, times(1)).findById(1L);
        logger.info("testFindById_Success completed successfully.");
    }

    @Test
    @Order(4)
    void testFindById_Fail_NotFound() {
        logger.info("Running testFindById_Fail_NotFound...");

        when(bookInteractionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.NotFoundException.class, () -> bookInteractionService.getBookInteractionById(99L));
        verify(bookInteractionRepository, times(1)).findById(99L);
        logger.info("testFindById_Fail_NotFound completed.");
    }

    @Test
    @Order(5)
    void testGetAllBookInteractions_Success() {
        logger.info("Running testGetAllBookInteractions_Success...");
        List<BookInteractionEntity> interactions = Collections.singletonList(sampleInteractionEntity);
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookInteractionEntity> page = new PageImpl<>(interactions, pageable, interactions.size());

        when(bookInteractionRepository.findAll(pageable)).thenReturn(page);
        when(bookInteractionMapper.bookInteractionEntityToDTO(any(BookInteractionEntity.class))).thenReturn(sampleInteractionDTO);

        Page<BookInteractionDTO> result = bookInteractionService.getAllBookInteractions(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(sampleInteractionDTO.getId(), result.getContent().get(0).getId());

        verify(bookInteractionRepository, times(1)).findAll(pageable);
        verify(bookInteractionMapper, times(1)).bookInteractionEntityToDTO(any(BookInteractionEntity.class));

        logger.info("testGetAllBookInteractions_Success completed successfully.");
    }

    @Test
    @Order(6)
    void testUpdateBookInteraction_Success() {
        logger.info("Running testUpdateBookInteraction_Success...");

        sampleInteractionEntity.setDescription("Updated description");
        when(bookInteractionRepository.findById(1L)).thenReturn(Optional.of(sampleInteractionEntity));
        when(bookInteractionRepository.save(any())).thenReturn(sampleInteractionEntity);
        when(bookInteractionMapper.bookInteractionEntityToDTO(any())).thenReturn(sampleInteractionDTO);

        BookInteractionDTO result = bookInteractionService.modifyBookInteraction(1L, sampleInteractionDTO);

        assertNotNull(result);
        assertEquals("TEST", result.getDescription());
        verify(bookInteractionRepository, times(1)).save(any());
        logger.info("testUpdateBookInteraction_Success completed successfully.");
    }

    @Test
    @Order(7)
    void testUpdateBookInteraction_Fail_NotFound() {
        logger.info("Running testUpdateBookInteraction_Fail_NotFound...");

        when(bookInteractionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.InternalServerErrorException.class, () -> bookInteractionService.modifyBookInteraction(99L, sampleInteractionDTO));
        verify(bookInteractionRepository, never()).save(any());
        logger.info("testUpdateBookInteraction_Fail_NotFound completed.");
    }

    @Test
    @Order(8)
    void testDeleteById_Success() {
        logger.info("Running testDeleteById_Success...");

        when(bookInteractionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookInteractionRepository).deleteById(1L);

        assertDoesNotThrow(() -> bookInteractionService.removeBookInteraction(1L));
        verify(bookInteractionRepository, times(1)).deleteById(1L);
        logger.info("testDeleteById_Success completed successfully.");
    }

    @Test
    @Order(9)
    void testDeleteById_Fail_NotFound() {
        logger.info("Running testDeleteById_Fail_NotFound...");

        when(bookInteractionRepository.existsById(99L)).thenReturn(false);

        assertThrows(CustomExceptions.NotFoundException.class, () -> bookInteractionService.removeBookInteraction(99L));
        verify(bookInteractionRepository, never()).deleteById(99L);
        logger.info("testDeleteById_Fail_NotFound completed.");
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