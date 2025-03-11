package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.entity.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookServiceInterface {

    BookDTO createBook(BookDTO bookDTO);
    BookDTO getBookById(Long id);
    Page<BookDTO> getAllBooks(Pageable pageable);
    Page<BookDTO> getBookByTitleAndAuthor(String title, String author, Pageable pageable);
    List<BookDTO> searchBook(String searchTerm);
    Optional<BookDTO> getBookByIsbn(String isbn);
    Page<BookDTO> getBookByPageCountBetween(int minPageCount, int maxPageCount, Pageable pageable);
    Page<BookDTO> getBookByPublishDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<BookDTO> getBookByGenre(Genre genre, Pageable pageable);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
}