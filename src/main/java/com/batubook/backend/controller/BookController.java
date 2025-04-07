package com.batubook.backend.controller;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.service.serviceImplementation.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookServiceImpl bookService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @PostMapping("/create")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        logger.info("Received request to create a new book with title: {}", bookDTO.getTitle());
        BookDTO createdBook = bookService.registerBook(bookDTO);
        logger.info("Successfully created book with ID: {}", createdBook.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> fetchBookById(@PathVariable Long id) {
        logger.info("Received GET request for /api/books/{}", id);
        BookDTO bookDTO = bookService.getBookById(id);
        logger.info("Returned response for book with ID: {}", id);
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping
    public ResponseEntity<Page<BookDTO>> fetchAllBooks(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/books called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookDTO> allBooks = bookService.getAllBooks(pageable);
        logger.info("Successfully fetched {} books", allBooks.getNumberOfElements());
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping("/BookTitleAndAuthor")
    public ResponseEntity<Page<BookDTO>> fetchBookByTitleAndAuthor(
            @RequestParam String title,
            @RequestParam String author,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.debug("Received request to search books by title and author: title = '{}', author = '{}', Page number = {}, Page size = {}",
                title, author, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookDTO> titleAndAuthor = bookService.getBookByTitleAndAuthor(title, author, pageable);
        logger.info("Successfully fetched {} books for title: '{}' and author: '{}'", titleAndAuthor.getTotalElements(), title, author);
        return ResponseEntity.ok(titleAndAuthor);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> fetchBooksByCriteria(
            @RequestParam String searchTerm,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to search for books with search term: '{}' and pagination: Page number = {}, Page size = {}",
                searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookDTO> books = bookService.getBookByCriteria(searchTerm, pageable);
        logger.info("Successfully fetched {} books matching the search term: '{}'", books.getTotalElements(), searchTerm);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/Isbn/{isbn}")
    public ResponseEntity<BookDTO> fetchBookByIsbn(@PathVariable String isbn) {
        logger.info("Received request to search for book with ISBN: '{}'", isbn);
        BookDTO bookDTO = bookService.getBookByIsbn(isbn);
        logger.info("Successfully found book with ISBN: '{}'", isbn);
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping("/pageCountBetween")
    public ResponseEntity<Page<BookDTO>> fetchBooksByPageCountBetween(
            @RequestParam int minPageCount,
            @RequestParam int maxPageCount,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to search books with page count between {} and {}", minPageCount, maxPageCount);
        Page<BookDTO> books = bookService.getBookByPageCountBetween(minPageCount, maxPageCount, pageable);
        logger.info("Successfully fetched {} books with page count between {} and {}", books.getTotalElements(), minPageCount, maxPageCount);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/publishDateBetween")
    public ResponseEntity<Page<BookDTO>> fetchBooksByPublishDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to search books published between '{}' and '{}'", startDate, endDate);
        Page<BookDTO> books = bookService.getBookByPublishDateBetween(startDate, endDate, pageable);
        logger.info("Successfully fetched {} books published between '{}' and '{}'", books.getTotalElements(), startDate, endDate);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/genre")
    public ResponseEntity<Page<BookDTO>> fetchBooksByGenre(
            @RequestParam String genre,
            @PageableDefault(size = 5) Pageable pageable) {
        Genre genreEnum = Genre.fromString(genre);
        logger.info("Received request to search books of genre '{}' with pagination: page number = {}, page size = {}",
                genreEnum, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookDTO> books = bookService.getBookByGenre(genreEnum, pageable);
        logger.info("Successfully fetched {} books for genre '{}'", books.getTotalElements(), genreEnum);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        logger.info("Received request to update book with id: {}", id);
        BookDTO updatedBook = bookService.modifyBook(id, bookDTO);
        logger.info("Successfully updated book with id: {}", id);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        logger.info("Received request to delete book with ID: {}", id);
        bookService.removeBook(id);
        logger.info("Successfully deleted book with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}