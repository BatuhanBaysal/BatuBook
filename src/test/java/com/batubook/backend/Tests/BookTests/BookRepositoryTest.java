package com.batubook.backend.Tests.BookTests;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(BookRepositoryTest.class);

    @Autowired
    private BookRepository bookRepository;

    private BookEntity book;

    @BeforeAll
    static void beforeAll() {
        logger.info("BookRepository test suite started.");
    }

    @BeforeEach
    void setup() {
        logger.info("Setting up initial book entity");
        book = createTestBook();
        bookRepository.saveAndFlush(book);
    }

    @AfterEach
    void cleanUp() {
        logger.info("Cleaning up database after test");
        bookRepository.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        logger.info("BookRepository test suite completed.");
    }

    private BookEntity createTestBook() {
        return BookEntity.builder()
                .title("1984")
                .author("George Orwell")
                .isbn("1234567890")
                .pageCount(352)
                .publishDate(LocalDate.of(1949, 6, 8))
                .genre(Genre.DYSTOPIA)
                .bookCoverImageUrl(" https://tr.wikipedia.org/wiki/Dosya:1984.jpg ")
                .summary(" More broadly, the novel examines the role of truth and facts within societies and the ways in which they can be manipulated. ")
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Should save and retrieve all book fields correctly")
    void shouldSaveAndRetrieveAllFields() {
        logger.info("Running test: Should save and retrieve all book fields correctly");

        Optional<BookEntity> foundBook = bookRepository.findById(book.getId());

        assertThat(foundBook).isPresent();
        BookEntity b = foundBook.get();

        assertAll("Check all fields",
                () -> assertThat(b.getTitle()).isEqualTo("1984"),
                () -> assertThat(b.getAuthor()).isEqualTo("George Orwell"),
                () -> assertThat(b.getIsbn()).isEqualTo("1234567890"),
                () -> assertThat(b.getPageCount()).isEqualTo(352),
                () -> assertThat(b.getPublishDate()).isEqualTo(LocalDate.of(1949, 6, 8)),
                () -> assertThat(b.getGenre()).isEqualTo(Genre.DYSTOPIA),
                () -> assertThat(b.getBookCoverImageUrl()).isEqualTo("https://tr.wikipedia.org/wiki/Dosya:1984.jpg"),
                () -> assertThat(b.getSummary()).isEqualTo("More broadly, the novel examines the role of truth and facts within societies and the ways in which they can be manipulated.")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should trim string fields on save")
    void shouldTrimStringFields() {
        logger.info("Running test: Should trim string fields on save");

        BookEntity found = bookRepository.findById(book.getId()).orElseThrow();

        assertAll("Trimmed Fields",
                () -> assertThat(found.getTitle()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(found.getAuthor()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(found.getIsbn()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(found.getBookCoverImageUrl()).doesNotStartWith(" ").doesNotEndWith(" "),
                () -> assertThat(found.getSummary()).doesNotStartWith(" ").doesNotEndWith(" ")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Should find book by ISBN")
    void shouldFindBookByIsbn() {
        logger.info("Running test: Should find book by ISBN");

        Optional<BookEntity> found = bookRepository.findByIsbn("1234567890");

        assertAll("Find by ISBN",
                () -> assertThat(found).isPresent(),
                () -> assertThat(found.get().getTitle()).isEqualTo("1984")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should return empty when book is not found by ID")
    void shouldReturnEmptyWhenBookNotFoundById() {
        logger.info("Running test: Should return empty when book is not found by ID");

        Optional<BookEntity> notFound = bookRepository.findById(999L);
        assertThat(notFound).isNotPresent();
    }

    @Test
    @Order(5)
    @DisplayName("Should find books by title and author ignoring case")
    void shouldFindBooksByTitleAndAuthorIgnoreCase() {
        logger.info("Running test: Should find books by title and author ignoring case");

        bookRepository.findAll().forEach(b -> {
            logger.info("Saved Title: [{}]", b.getTitle());
            logger.info("Saved Author: [{}]", b.getAuthor());
        });

        Page<BookEntity> result = bookRepository.findByTitleAndAuthorIgnoreCase("1984", "George Orwell", Pageable.unpaged());
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIsbn()).isEqualTo("1234567890");
    }

    @Test
    @Order(6)
    @DisplayName("Should find books by page count range")
    void shouldFindBooksByPageCountRange() {
        logger.info("Running test: Should find books by page count range");

        Page<BookEntity> result = bookRepository.findByPageCountBetween(300, 400, Pageable.unpaged());
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getPageCount()).isBetween(300, 400);
    }

    @Test
    @Order(7)
    @DisplayName("Should find books by publish date range")
    void shouldFindBooksByPublishDateRange() {
        logger.info("Running test: Should find books by publish date range");

        Page<BookEntity> result = bookRepository.findByPublishDateBetween(
                LocalDate.of(1945, 1, 1),
                LocalDate.of(1984, 1, 1),
                Pageable.unpaged()
        );

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getPublishDate()).isBetween(LocalDate.of(1945, 1, 1), LocalDate.of(1984, 1, 1));
    }

    @Test
    @Order(8)
    @DisplayName("Should find books by genre")
    void shouldFindBooksByGenre() {
        logger.info("Running test: Should find books by genre");

        Page<BookEntity> result = bookRepository.findByGenre(Genre.DYSTOPIA, Pageable.unpaged());

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(b -> b.getGenre() == Genre.DYSTOPIA);
    }

    @Test
    @Order(9)
    @DisplayName("Should paginate and sort books correctly")
    void shouldPaginateAndSortBooksCorrectly() {
        logger.info("Running test: Should paginate and sort books correctly");

        bookRepository.saveAll(List.of(
                BookEntity.builder()
                        .title("A Tale of Two Cities")
                        .author("Charles Dickens")
                        .isbn("1111111111")
                        .pageCount(489)
                        .publishDate(LocalDate.of(1859, 4, 30))
                        .genre(Genre.NOVEL)
                        .build(),
                BookEntity.builder()
                        .title("Brave New World")
                        .author("Aldous Huxley")
                        .isbn("2222222222")
                        .pageCount(311)
                        .publishDate(LocalDate.of(1932, 8, 18))
                        .genre(Genre.DYSTOPIA)
                        .build()
        ));

        Pageable pageable = PageRequest.of(0, 3, Sort.by("title").ascending());
        Page<BookEntity> result = bookRepository.findAll(pageable);

        assertThat(result.getContent()).hasSize(3);

        List<String> titles = result.getContent().stream().map(BookEntity::getTitle).toList();
        assertThat(titles).containsExactly("1984", "A Tale of Two Cities", "Brave New World");
    }
}