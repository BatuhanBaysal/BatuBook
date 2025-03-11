package com.batubook.backend.repository;

import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Page<BookEntity> findByTitleAndAuthorIgnoreCase(String title, String author, Pageable pageable);
    Optional<BookEntity> findByIsbn(String isbn);
    Page<BookEntity> findByPageCountBetween(int minPageCount, int maxPageCount, Pageable pageable);
    Page<BookEntity> findByPublishDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<BookEntity> findByGenre(Genre genre, Pageable pageable);
}