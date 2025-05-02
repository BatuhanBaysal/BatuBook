package com.batubook.backend.Tests.BookSalesTest;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.dto.BookSalesDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.BookSalesEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.BookSalesMapper;
import com.batubook.backend.repository.BookSalesRepository;
import com.batubook.backend.service.serviceImplementation.BookSalesServiceImpl;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookSalesServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BookSalesServiceTest.class);

    @Mock
    private BookSalesRepository bookSalesRepository;

    @Mock
    private BookSalesMapper bookSalesMapper;

    @InjectMocks
    private BookSalesServiceImpl bookSalesService;

    @BeforeEach
    void setUp() {
        logger.info("Setting up the test environment...");
        reset(bookSalesRepository, bookSalesMapper);
        logger.info("Test environment setup complete.");
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up after test...");
        reset(bookSalesRepository, bookSalesMapper);
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

    private BookSalesEntity createTestBookSalesEntity() {
        return BookSalesEntity.builder()
                .salesCode("1234567")
                .publisher("TestPublisher")
                .price(20.5)
                .stockQuantity(3)
                .currency(BookSalesEntity.Currency.USD)
                .discount(10.0)
                .book(createTestBookEntity())
                .build();
    }

    private BookSalesDTO createTestBookSalesDTO() {
        return BookSalesDTO.builder()
                .salesCode("1234567")
                .publisher("TestPublisher")
                .price(20.5)
                .stockQuantity(3)
                .currency(BookSalesEntity.Currency.USD)
                .discount(10.0)
                .isAvailable(true)
                .bookId(createTestBookDTO().getId())
                .build();
    }

    @Test
    @Order(1)
    void testRegisterBookSales_Success() {
        BookSalesDTO dto = createTestBookSalesDTO();
        BookSalesEntity entity = createTestBookSalesEntity();

        when(bookSalesMapper.bookSalesDTOToEntity(dto)).thenReturn(entity);
        when(bookSalesRepository.save(entity)).thenReturn(entity);
        when(bookSalesMapper.bookSalesEntityToDTO(entity)).thenReturn(dto);

        BookSalesDTO result = bookSalesService.registerBookSales(dto);

        assertEquals(dto.getSalesCode(), result.getSalesCode());
        verify(bookSalesRepository).save(entity);
    }

    @Test
    @Order(2)
    void testRegisterBookSales_Failure() {
        BookSalesDTO dto = createTestBookSalesDTO();
        when(bookSalesMapper.bookSalesDTOToEntity(dto)).thenThrow(new RuntimeException("Mapping failed"));

        assertThrows(RuntimeException.class, () -> bookSalesService.registerBookSales(dto));
        verify(bookSalesRepository, never()).save(any());
    }

    @Test
    @Order(3)
    void testGetBookSalesById_Success() {
        Long id = 1L;
        BookSalesEntity entity = createTestBookSalesEntity();
        BookSalesDTO dto = createTestBookSalesDTO();

        when(bookSalesRepository.findById(id)).thenReturn(Optional.of(entity));
        when(bookSalesMapper.bookSalesEntityToDTO(entity)).thenReturn(dto);

        BookSalesDTO result = bookSalesService.getBookSalesById(id);

        assertEquals(dto.getSalesCode(), result.getSalesCode());
    }

    @Test
    @Order(4)
    void testGetBookSalesById_NotFound() {
        Long id = 1L;
        when(bookSalesRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookSalesService.getBookSalesById(id));
    }

    @Test
    @Order(5)
    void testGetAllBookSales_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookSalesEntity> entities = List.of(createTestBookSalesEntity());
        Page<BookSalesEntity> page = new PageImpl<>(entities);

        when(bookSalesRepository.findAll(pageable)).thenReturn(page);
        when(bookSalesMapper.bookSalesEntityToDTO(any())).thenReturn(createTestBookSalesDTO());

        Page<BookSalesDTO> result = bookSalesService.getAllBookSales(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @Order(6)
    void testGetBookSalesBySalesCode_Success() {
        String salesCode = "1234567";
        BookSalesEntity entity = createTestBookSalesEntity();
        BookSalesDTO dto = createTestBookSalesDTO();

        when(bookSalesRepository.findBySalesCode(salesCode)).thenReturn(Optional.of(entity));
        when(bookSalesMapper.bookSalesEntityToDTO(entity)).thenReturn(dto);

        BookSalesDTO result = bookSalesService.getBookSalesBySalesCode(salesCode);

        assertEquals(dto.getSalesCode(), result.getSalesCode());
    }

    @Test
    @Order(7)
    void testGetBookSalesBySalesCode_NotFound() {
        String salesCode = "nonexistent";
        when(bookSalesRepository.findBySalesCode(salesCode)).thenReturn(Optional.empty());

        assertThrows(CustomExceptions.NotFoundException.class, () -> bookSalesService.getBookSalesBySalesCode(salesCode));
    }

    @Test
    @Order(8)
    void testModifyBookSales_Success() {
        Long id = 1L;
        BookSalesDTO dto = createTestBookSalesDTO();
        BookSalesEntity entity = createTestBookSalesEntity();

        when(bookSalesRepository.findById(id)).thenReturn(Optional.of(entity));
        when(bookSalesRepository.save(any())).thenReturn(entity);
        when(bookSalesMapper.bookSalesEntityToDTO(entity)).thenReturn(dto);

        BookSalesDTO result = bookSalesService.modifyBookSales(id, dto);

        assertEquals(dto.getSalesCode(), result.getSalesCode());
    }

    @Test
    @Order(9)
    void testModifyBookSales_NotFound() {
        Long id = 1L;
        BookSalesDTO dto = createTestBookSalesDTO();

        when(bookSalesRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookSalesService.modifyBookSales(id, dto));
    }

    @Test
    @Order(10)
    void testRemoveBookSales_Success() {
        Long id = 1L;

        when(bookSalesRepository.existsById(id)).thenReturn(true);
        doNothing().when(bookSalesRepository).deleteById(id);

        bookSalesService.removeBookSales(id);

        verify(bookSalesRepository).deleteById(id);
    }

    @Test
    @Order(11)
    void testRemoveBookSales_NotFound() {
        Long id = 1L;
        when(bookSalesRepository.existsById(id)).thenReturn(false);

        assertThrows(CustomExceptions.NotFoundException.class, () -> bookSalesService.removeBookSales(id));
    }
}