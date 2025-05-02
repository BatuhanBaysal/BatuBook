package com.batubook.backend.Tests.ReviewTests;

import com.batubook.backend.dto.*;
import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.Gender;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.ReviewMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.ReviewRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceImplementation.ReviewServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceTest.class);

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;


    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewEntity sampleReviewEntity;
    private ReviewDTO sampleReviewDTO;

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment...");
        reset(reviewRepository, reviewMapper, userRepository, bookRepository);
        initializeMockData();
        logger.info("Test environment setup complete.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up after test...");
        reset(reviewRepository, reviewMapper, userRepository, bookRepository);
        logger.info("Cleanup complete.");
    }

    private void initializeMockData() {
        sampleReviewEntity = ReviewEntity.builder()
                .id(1L)
                .user(createSampleUserEntity())
                .book(createTestBookEntity())
                .reviewText("Test review")
                .rating(BigDecimal.valueOf(4.5))
                .build();

        sampleReviewDTO = ReviewDTO.builder()
                .id(1L)
                .userId(createSampleUserDTO().getId())
                .bookId(createTestBookDTO().getId())
                .reviewText("Test review")
                .rating(BigDecimal.valueOf(4.5))
                .build();
    }

    @Test
    @Order(1)
    void testRegisterReview_Success() {
        sampleReviewDTO.setUserId(1L);
        sampleReviewDTO.setBookId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(createSampleUserEntity()));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(createTestBookEntity()));
        when(reviewMapper.reviewDTOToEntity(sampleReviewDTO)).thenReturn(sampleReviewEntity);
        when(reviewRepository.save(any())).thenReturn(sampleReviewEntity);
        when(reviewMapper.reviewEntityToDTO(sampleReviewEntity)).thenReturn(sampleReviewDTO);

        ReviewDTO result = reviewService.registerReview(sampleReviewDTO);

        assertNotNull(result);
        assertEquals(sampleReviewDTO.getRating(), result.getRating());
        verify(reviewRepository).save(any());
    }

    @Test
    @Order(2)
    void testRegisterReview_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CustomExceptions.InternalServerErrorException exception = assertThrows(CustomExceptions.InternalServerErrorException.class, () -> {
            reviewService.registerReview(sampleReviewDTO);
        });

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @Order(3)
    void testRegisterReview_BookNotFound() {
        sampleReviewDTO.setUserId(1L);
        sampleReviewDTO.setBookId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(createSampleUserEntity()));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        CustomExceptions.InternalServerErrorException exception = assertThrows(CustomExceptions.InternalServerErrorException.class, () -> {
            reviewService.registerReview(sampleReviewDTO);
        });

        assertTrue(exception.getMessage().contains("Book not found"));
    }

    @Test
    @Order(4)
    void testGetReviewById_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(sampleReviewEntity));
        when(reviewMapper.reviewEntityToDTO(sampleReviewEntity)).thenReturn(sampleReviewDTO);

        ReviewDTO result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @Order(5)
    void testGetReviewById_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reviewService.getReviewById(1L);
        });

        assertTrue(exception.getMessage().contains("Review with ID"));
    }

    @Test
    @Order(6)
    void testGetAllReviews_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewEntity> reviewPage = new PageImpl<>(List.of(sampleReviewEntity));

        when(reviewRepository.findAll(pageable)).thenReturn(reviewPage);
        when(reviewMapper.reviewEntityToDTO(sampleReviewEntity)).thenReturn(sampleReviewDTO);

        Page<ReviewDTO> result = reviewService.getAllReviews(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(sampleReviewDTO.getId(), result.getContent().get(0).getId());
    }

    @Test
    @Order(7)
    void testGetReviewByRating_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ReviewEntity> reviewPage = new PageImpl<>(List.of(sampleReviewEntity));

        when(reviewRepository.findByRating(BigDecimal.valueOf(4.5), pageable)).thenReturn(reviewPage);
        when(reviewMapper.reviewEntityToDTO(sampleReviewEntity)).thenReturn(sampleReviewDTO);

        Page<ReviewDTO> result = reviewService.getReviewByRating(BigDecimal.valueOf(4.5), pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    @Order(8)
    void testModifyReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(sampleReviewEntity));
        when(reviewRepository.save(any())).thenReturn(sampleReviewEntity);
        when(reviewMapper.reviewEntityToDTO(sampleReviewEntity)).thenReturn(sampleReviewDTO);

        ReviewDTO result = reviewService.modifyReview(1L, sampleReviewDTO);

        assertNotNull(result);
        assertEquals(sampleReviewDTO.getReviewText(), result.getReviewText());
    }

    @Test
    @Order(9)
    void testModifyReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        CustomExceptions.InternalServerErrorException exception = assertThrows(CustomExceptions.InternalServerErrorException.class, () -> {
            reviewService.modifyReview(1L, sampleReviewDTO);
        });

        assertTrue(exception.getMessage().contains("Review not found"));
    }

    @Test
    @Order(10)
    void testRemoveReview_Success() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        assertDoesNotThrow(() -> reviewService.removeReview(1L));
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    @Order(11)
    void testRemoveReview_NotFound() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        CustomExceptions.NotFoundException exception = assertThrows(CustomExceptions.NotFoundException.class, () -> {
            reviewService.removeReview(1L);
        });

        assertTrue(exception.getMessage().contains("Review not found"));
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