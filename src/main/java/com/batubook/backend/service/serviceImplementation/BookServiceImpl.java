package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.BookMapper;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.service.serviceInterface.BookServiceInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
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
    public BookDTO registerBook(BookDTO bookDTO) {
        logger.info("Creating a new book with title: {}", bookDTO.getTitle());
        try {
            BookEntity bookEntity = bookMapper.bookDTOToEntity(bookDTO);
            logger.debug("Converted BookDTO to BookEntity: {}", bookEntity);
            BookEntity savedBook = bookRepository.save(bookEntity);
            logger.info("Book saved successfully with ID: {}", savedBook.getId());
            return bookMapper.bookEntityToDTO(savedBook);

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating book: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Book could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        logger.info("Attempting to retrieve book with ID: {}", id);
        BookEntity bookEntity = bookRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Book not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("Book not found with ID: " + id);
                });

        logger.info("Successfully retrieved book with ID: {}", id);
        return bookMapper.bookEntityToDTO(bookEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        logger.debug("Fetching all books with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookEntity> allBooks = bookRepository.findAll(pageable);
        logger.info("Successfully fetched {} books", allBooks.getNumberOfElements());
        return allBooks.map(bookMapper::bookEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getBookByTitleAndAuthor(String title, String author, Pageable pageable) {
        logger.debug("Searching for books with title '{}' and author '{}' using pagination: Page number = {}, Page size = {}",
                title, author, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookEntity> books = bookRepository.findByTitleAndAuthorIgnoreCase(title, author, pageable);
        logger.info("Successfully fetched {} books with the given title '{}' and author '{}'", books.getTotalElements(), title, author);
        return books.map(bookMapper::bookEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getBookByCriteria(String searchTerm, Pageable pageable) {
        logger.info("Searching books with search term: '{}' using pagination: page number = {}, page size = {}",
                searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<BookEntity> query = cb.createQuery(BookEntity.class);
            Root<BookEntity> book = query.from(BookEntity.class);

            Predicate titlePredicate = cb.like(cb.lower(book.get("title")), "%" + searchTerm.toLowerCase() + "%");
            Predicate authorPredicate = cb.like(cb.lower(book.get("author")), "%" + searchTerm.toLowerCase() + "%");
            query.where(cb.or(titlePredicate, authorPredicate));

            TypedQuery<BookEntity> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<BookEntity> books = typedQuery.getResultList();
            logger.info("Found {} books for search term '{}'", books.size(), searchTerm);
            return new PageImpl<>(books.stream()
                    .map(bookMapper::bookEntityToDTO)
                    .collect(Collectors.toList()), pageable, books.size());

        } catch (Exception e) {
            logger.error("Error occurred while searching for books with search term '{}'", searchTerm, e);
            throw new RuntimeException("Error occurred while searching for books", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookByIsbn(String isbn) {
        logger.info("Searching for book with ISBN: '{}'", isbn);
        BookEntity bookEntity = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("Book not found with ISBN: " + isbn));

        logger.info("Found book with ISBN: '{}'", isbn);
        return bookMapper.bookEntityToDTO(bookEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getBookByPageCountBetween(int minPageCount, int maxPageCount, Pageable pageable) {
        logger.info("Searching for books with page count between {} and {} using pagination: page number = {}, page size = {}",
                minPageCount, maxPageCount, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookEntity> books = bookRepository.findByPageCountBetween(minPageCount, maxPageCount, pageable);
        logger.info("Found {} books with page count between {} and {}", books.getTotalElements(), minPageCount, maxPageCount);
        return books.map(bookMapper::bookEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getBookByPublishDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        logger.info("Searching for books published between '{}' and '{}' with pagination: page number = {}, page size = {}",
                startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookEntity> books = bookRepository.findByPublishDateBetween(startDate, endDate, pageable);
        logger.info("Found {} books published between '{}' and '{}'", books.getTotalElements(), startDate, endDate);
        return books.map(bookMapper::bookEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDTO> getBookByGenre(Genre genre, Pageable pageable) {
        logger.info("Searching for books of genre '{}' with pagination: page number = {}, page size = {}",
                genre, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookEntity> books = bookRepository.findByGenre(genre, pageable);
        logger.info("Found {} books of genre '{}'", books.getTotalElements(), genre);
        return books.map(bookMapper::bookEntityToDTO);
    }

    @Override
    public BookDTO modifyBook(Long id, BookDTO bookDTO) {
        logger.info("Attempting to update book with id: {}", id);
        try {
            BookEntity existingBook = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));

            logger.info("Updating book with id: {}. New details: Title = '{}', Author = '{}', ISBN = '{}', Page Count = '{}', Publish Date = '{}', Genre = '{}'",
                    id, bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getIsbn(), bookDTO.getPageCount(), bookDTO.getPublishDate(), bookDTO.getGenre());

            updateBookDetails(existingBook, bookDTO);
            BookEntity updatedBook = bookRepository.save(existingBook);
            logger.info("Successfully updated book with id: {}", id);
            return bookMapper.bookEntityToDTO(updatedBook);

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Book not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating book: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Book could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeBook(Long id) {
        logger.info("Attempting to remove book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            logger.error("Book with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Book not found with ID: " + id);
        }

        bookRepository.deleteById(id);
        logger.info("Successfully deleted book with ID: {}", id);
    }

    private void updateBookDetails(BookEntity bookEntity, BookDTO bookDTO) {
        if (bookDTO.getBookCoverImageUrl() != null) {
            bookEntity.setBookCoverImageUrl(bookDTO.getBookCoverImageUrl());
        }
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
        if (bookDTO.getSummary() != null) {
            bookEntity.setSummary(bookDTO.getSummary());
        }
    }
}