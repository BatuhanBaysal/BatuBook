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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BookController {

    private final BookServiceImpl bookService;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @PostMapping("/createBook")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {

        logger.info("Received request to create a new book with title: {}", bookDTO.getTitle());
        try {
            BookDTO createdBook = bookService.createBook(bookDTO);
            logger.info("Successfully created book with ID: {}", createdBook.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (Exception e) {
            logger.error("Error occurred while creating book: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/bookId/{id}")
    public ResponseEntity<BookDTO> searchBookById(@PathVariable Long id) {

        logger.info("Received request to search for book with ID: {}", id);
        try {
            BookDTO searchBookId = bookService.getBookById(id);
            logger.info("Successfully retrieved book with ID: {}", id);
            return ResponseEntity.ok(searchBookId);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving book with ID: {}", id, e);
            throw e;
        }
    }

    @GetMapping("/allBooks")
    public ResponseEntity<Page<BookDTO>> searchAllBooks(Pageable pageable) {

        logger.info("Received request to fetch all books with pagination: Page number = {}, Page size = {}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookDTO> allBooks = bookService.getAllBooks(pageable);
            logger.info("Successfully fetched {} books", allBooks.getTotalElements());
            return ResponseEntity.ok(allBooks);
        } catch (Exception e) {
            logger.error("Error occurred while fetching books with pagination", e);
            throw e;
        }
    }

    @GetMapping("/BookTitleAndAuthor")
    public ResponseEntity<Page<BookDTO>> searchBookByTitleAndAuthor(
            @RequestParam String title, @RequestParam String author, Pageable pageable) {

        logger.info("Received request to search books by title and author: title = {}, author = {}, Page number = {}, Page size = {}",
                title, author, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookDTO> titleAndAuthor = bookService.getBookByTitleAndAuthor(title, author, pageable);
            logger.info("Successfully fetched {} books for title: '{}' and author: '{}'", titleAndAuthor.getTotalElements(), title, author);
            return ResponseEntity.ok(titleAndAuthor);
        } catch (Exception e) {
            logger.error("Error occurred while searching books by title: '{}' and author: '{}'", title, author, e);
            throw e;
        }
    }

    @GetMapping("/searchTerm")
    public ResponseEntity<List<BookDTO>> searchTerm(@RequestParam String searchTerm) {

        logger.info("Received request to search books with search term: '{}'", searchTerm);
        try {
            List<BookDTO> searchBook = bookService.searchBook(searchTerm);
            logger.info("Successfully fetched {} books matching the search term: '{}'", searchBook.size(), searchTerm);

            return ResponseEntity.ok(searchBook);
        } catch (Exception e) {
            logger.error("Error occurred while searching books with search term: '{}'", searchTerm, e);
            throw e;
        }
    }

    @GetMapping("/bookIsbn/{isbn}")
    public ResponseEntity<BookDTO> searchBookByIsbn(@PathVariable String isbn) {

        logger.info("Received request to search for book with ISBN: '{}'", isbn);
        try {
            Optional<BookDTO> bookDTO = bookService.getBookByIsbn(isbn);
            if (bookDTO.isPresent()) {
                logger.info("Successfully found book with ISBN: '{}'", isbn);
                return ResponseEntity.ok(bookDTO.get());
            } else {
                logger.info("No book found with ISBN: '{}'", isbn);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while searching for book with ISBN: '{}'", isbn, e);
            throw e;
        }
    }

    @GetMapping("/pageCountBetween")
    public ResponseEntity<Page<BookDTO>> searchBooksByPageCountBetween(
            @RequestParam int minPageCount, @RequestParam int maxPageCount, Pageable pageable) {

        logger.info("Received request to search books with page count between {} and {}", minPageCount, maxPageCount);
        try {
            Page<BookDTO> books = bookService.getBookByPageCountBetween(minPageCount, maxPageCount, pageable);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error occurred while searching books with page count between {} and {}", minPageCount, maxPageCount, e);
            throw e;
        }
    }

    @GetMapping("/publishDateBetween")
    public ResponseEntity<Page<BookDTO>> searchBooksByPublishDateBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {

        logger.info("Received request to search books published between '{}' and '{}'", startDate, endDate);
        try {
            Page<BookDTO> books = bookService.getBookByPublishDateBetween(startDate, endDate, pageable);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            logger.error("Error occurred while searching books published between '{}' and '{}'", startDate, endDate, e);
            throw e;
        }
    }

    @GetMapping("/genre")
    public ResponseEntity<Page<BookDTO>> searchBooksByGenre(
            @RequestParam String genre, Pageable pageable) {

        try {
            Genre genreEnum = null;
            try {
                genreEnum = Genre.valueOf(genre.toUpperCase().replace("İ", "I"));
            } catch (IllegalArgumentException e) {
                logger.error("Invalid genre value: {}. Please provide a valid genre.", genre, e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            logger.info("Received request to search books of genre '{}'", genreEnum);
            Page<BookDTO> books = bookService.getBookByGenre(genreEnum, pageable);
            return ResponseEntity.ok(books);

        } catch (Exception e) {
            logger.error("Error occurred while searching books of genre '{}'", genre, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updateBook/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {

        logger.info("Received request to update book with id: {}", id);
        try {
            BookDTO updatedBook = bookService.updateBook(id, bookDTO);
            logger.info("Successfully updated book with id: {}", id);
            return ResponseEntity.ok(updatedBook);
        } catch (Exception e) {
            logger.error("Error occurred while updating book with id: {}", id, e);
            throw e;
        }
    }

    @DeleteMapping("/deleteBook/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {

        logger.info("Received request to delete book with id: {}", id);
        try {
            bookService.deleteBook(id);
            logger.info("Successfully deleted book with id: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Book deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting book with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting the book");
        }
    }
}