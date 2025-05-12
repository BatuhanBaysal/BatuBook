package com.batubook.backend.Tests.BookSalesTest;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.BookSalesEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.BookSalesRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookSalesRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(BookSalesRepositoryTest.class);

    @Autowired
    private BookSalesRepository bookSalesRepository;

    @Autowired
    private BookRepository bookRepository;

    private BookEntity book;

    @BeforeEach
    void setUp() {
        logger.info("Creating test data...");

        book = BookEntity.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("1234567890")
                .pageCount(352)
                .publishDate(LocalDate.of(1949, 6, 8))
                .genre(Genre.DYSTOPIA)
                .bookCoverImageUrl(" https://tr.wikipedia.org/wiki/Dosya:1984.jpg ")
                .summary(" More broadly, the novel examines the role of truth and facts within societies and the ways in which they can be manipulated. ")
                .build();

        book = bookRepository.save(book);

        BookSalesEntity sale = BookSalesEntity.builder()
                .salesCode("SALE001")
                .publisher("Test Publisher")
                .price(120.0)
                .stockQuantity(50)
                .currency(BookSalesEntity.Currency.USD)
                .discount(15.0)
                .isAvailable(true)
                .book(book)
                .build();

        bookSalesRepository.save(sale);
    }

    @AfterEach
    void tearDown() {
        logger.info("Cleaning up test data...");
        bookSalesRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should find BookSales by sales code")
    void shouldFindBySalesCode() {
        Optional<BookSalesEntity> result = bookSalesRepository.findBySalesCode("SALE001");

        assertThat(result).isPresent();
        assertThat(result.get().getSalesCode()).isEqualTo("SALE001");
        assertThat(result.get().getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Should find BookSales by book ID")
    void shouldFindByBookId() {
        Page<BookSalesEntity> result = bookSalesRepository.findByBookId(book.getId(), Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBook().getId()).isEqualTo(book.getId());
    }

    @Test
    @Order(3)
    @DisplayName("Should find BookSales with price greater than specified value")
    void shouldFindByPriceGreaterThanOrderByPriceDesc() {
        Page<BookSalesEntity> result = bookSalesRepository.findByPriceGreaterThanOrderByPriceDesc(100.0, Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPrice()).isGreaterThan(100.0);
    }

    @Test
    @Order(4)
    @DisplayName("Should find available BookSales")
    void shouldFindByIsAvailableTrue() {
        Page<BookSalesEntity> result = bookSalesRepository.findByIsAvailableTrue(Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsAvailable()).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("Should find BookSales with discount greater than specified value")
    void shouldFindByDiscountGreaterThan() {
        Page<BookSalesEntity> result = bookSalesRepository.findByDiscountGreaterThan(10.0, Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDiscount()).isGreaterThan(10.0);
    }
}