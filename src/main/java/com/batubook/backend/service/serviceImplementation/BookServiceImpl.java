package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.mapper.BookMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.service.serviceInterface.BookServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookServiceInterface {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {

        logger.info("Creating a new book with title: {}", bookDTO.getTitle());
        try {
            BookEntity bookEntity = bookMapper.bookDTOToBookEntity(bookDTO);
            logger.debug("Converted BookDTO to BookEntity: {}", bookEntity);

            BookEntity savedBook = bookRepository.save(bookEntity);
            logger.info("Book saved successfully with ID: {}", savedBook.getId());

            return bookMapper.bookEntityToBookDTO(savedBook);
        } catch (Exception e) {
            logger.error("Error occurred while creating the book: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while creating the book", e);
        }
    }

    @Override
    public BookDTO getBookById(Long id) {

        logger.info("Fetching book with ID: {}", id);
        try {
            BookEntity bookEntity = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book with ID " + id + " not found"));

            logger.info("Book with ID: {} found successfully", id);
            return bookMapper.bookEntityToBookDTO(bookEntity);
        } catch (EntityNotFoundException e) {
            logger.error("Book with ID: {} not found", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error occurred while fetching book with ID: {}", id, e);
            throw new RuntimeException("Error occurred while fetching the book", e);
        }
    }

    @Override
    public Page<BookDTO> getAllBooks(Pageable pageable) {

        logger.info("Fetching all books with pagination: Page number = {}, Page size = {}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookEntity> books = bookRepository.findAll(pageable);
            logger.info("Successfully fetched {} books", books.getTotalElements());

            return books.map(bookMapper::bookEntityToBookDTO);
        } catch (Exception e) {
            logger.error("Error occurred while fetching books with pagination", e);
            throw new RuntimeException("Error occurred while fetching books", e);
        }
    }

    @Override
    public Page<BookDTO> getBookByTitleAndAuthor(String title, String author, Pageable pageable) {

        logger.info("Searching books with title: '{}' and author: '{}' using pagination: Page number = {}, Page size = {}",
                title, author, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookEntity> books = bookRepository.findByTitleAndAuthorIgnoreCase(title, author, pageable);
            logger.info("Successfully fetched {} books with the given criteria", books.getTotalElements());

            return books.map(bookMapper::bookEntityToBookDTO);
        } catch (Exception e) {
            logger.error("Error occurred while searching books with title: '{}' and author: '{}'", title, author, e);
            throw new RuntimeException("Error occurred while searching books by title and author", e);
        }
    }

    @Override
    public List<BookDTO> searchBook(String searchTerm) {

        logger.info("Searching books with search term: '{}'", searchTerm);
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<BookEntity> query = cb.createQuery(BookEntity.class);
            Root<BookEntity> book = query.from(BookEntity.class);

            Predicate titlePredicate = cb.like(cb.lower(book.get("title")), "%" + searchTerm.toLowerCase() + "%");
            Predicate authorPredicate = cb.like(cb.lower(book.get("author")), "%" + searchTerm.toLowerCase() + "%");

            query.where(cb.or(titlePredicate, authorPredicate));
            List<BookEntity> books = entityManager.createQuery(query).getResultList();
            logger.info("Successfully found {} books for search term '{}'", books.size(), searchTerm);
            return books.stream().map(bookMapper::bookEntityToBookDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while searching for books with search term: '{}'", searchTerm, e);
            throw new RuntimeException("Error occurred while searching for books", e);
        }
    }

    @Override
    public Optional<BookDTO> getBookByIsbn(String isbn) {

        logger.info("Searching for book with ISBN: '{}'", isbn);
        try {
            Optional<BookEntity> bookEntity = bookRepository.findByIsbn(isbn);
            if (bookEntity.isPresent()) {
                logger.info("Found book with ISBN: '{}'", isbn);
            } else {
                logger.info("No book found with ISBN: '{}'", isbn);
            }

            return bookEntity.map(bookMapper::bookEntityToBookDTO);
        } catch (Exception e) {
            logger.error("Error occurred while searching for book with ISBN: '{}'", isbn, e);
            throw new RuntimeException("Error occurred while searching for book", e);
        }
    }

    @Override
    public Page<BookDTO> getBookByPageCountBetween(int minPageCount, int maxPageCount, Pageable pageable) {

        logger.info("Searching for books with page count between {} and {} using pagination: Page number = {}, Page size = {}",
                minPageCount, maxPageCount, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookEntity> books = bookRepository.findByPageCountBetween(minPageCount, maxPageCount, pageable);
            logger.info("Successfully found {} books with page count between {} and {}", books.getTotalElements(), minPageCount, maxPageCount);
            return books.map(bookMapper::bookEntityToBookDTO);
        } catch (Exception e) {
            logger.error("Error occurred while searching for books with page count between {} and {}", minPageCount, maxPageCount, e);
            throw new RuntimeException("Error occurred while searching for books", e);
        }
    }

    @Override
    public Page<BookDTO> getBookByPublishDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {

        logger.info("Searching for books published between '{}' and '{}' using pagination: Page number = {}, Page size = {}",
                startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookEntity> books = bookRepository.findByPublishDateBetween(startDate, endDate, pageable);
            logger.info("Successfully found {} books published between '{}' and '{}'", books.getTotalElements(), startDate, endDate);
            return books.map(bookMapper::bookEntityToBookDTO);
        } catch (Exception e) {
            logger.error("Error occurred while searching for books published between '{}' and '{}'", startDate, endDate, e);
            throw new RuntimeException("Error occurred while searching for books", e);
        }
    }

    @Override
    public Page<BookDTO> getBookByGenre(Genre genre, Pageable pageable) {

        logger.info("Searching for books of genre '{}' using pagination: Page number = {}, Page size = {}",
                genre, pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<BookEntity> books = bookRepository.findByGenre(genre, pageable);
            logger.info("Successfully found {} books of genre '{}'", books.getTotalElements(), genre);
            return books.map(bookMapper::bookEntityToBookDTO);
        } catch (Exception e) {
            logger.error("Error occurred while searching for books of genre '{}'", genre, e);
            throw new RuntimeException("Error occurred while searching for books by genre", e);
        }
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {

        logger.info("Attempting to update book with id: {}", id);
        try {
            BookEntity existingBook = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

            logger.info("Updating book with id: {}. New details: Title = '{}', Author = '{}', ISBN = '{}', Page Count = '{}', Publish Date = '{}', Genre = '{}'",
                    id, bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getIsbn(), bookDTO.getPageCount(), bookDTO.getPublishDate(), bookDTO.getGenre());

            updateBookDetails(existingBook, bookDTO);
            BookEntity updatedBook = bookRepository.save(existingBook);
            logger.info("Successfully updated book with id: {}", id);
            return bookMapper.bookEntityToBookDTO(updatedBook);
        } catch (Exception e) {
            logger.error("Error occurred while updating book with id: {}", id, e);
            throw new RuntimeException("Error occurred while updating the book", e);
        }
    }

    private void updateBookDetails(BookEntity bookEntity, BookDTO bookDTO) {

        if (bookDTO.getTitle() != null) {
            bookEntity.setTitle(bookDTO.getTitle());
        }
        if (bookDTO.getAuthor() != null) {
            bookEntity.setAuthor(bookDTO.getAuthor());
        }
        if (bookDTO.getIsbn() != null) {
            bookEntity.setIsbn(bookDTO.getIsbn());
        }
        if (bookDTO.getPageCount() != null) {
            bookEntity.setPageCount(bookDTO.getPageCount());
        }
        if (bookDTO.getPublishDate() != null) {
            try {
                bookEntity.setPublishDate(LocalDate.parse(bookDTO.getPublishDate(), DateTimeFormatter.ISO_DATE));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
        if (bookDTO.getGenre() != null) {
            bookEntity.setGenre(bookDTO.getGenre());
        }
    }

    @Override
    public void deleteBook(Long id) {

        logger.info("Attempting to delete book with id: {}", id);
        try {
            if (!bookRepository.existsById(id)) {
                logger.warn("Book with id: {} not found, cannot delete", id);
                throw new EntityNotFoundException("Book not found with ID: " + id);
            }

            bookRepository.deleteById(id);
            logger.info("Successfully deleted book with id: {}", id);
        } catch (Exception e) {
            logger.error("Error occurred while deleting book with id: {}", id, e);
            throw new RuntimeException("Error occurred while deleting the book", e);
        }
    }
}