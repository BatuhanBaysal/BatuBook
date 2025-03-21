package com.batubook.backend.repository;

import com.batubook.backend.entity.BookSalesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookSalesRepository extends JpaRepository<BookSalesEntity, Long> {

    Optional<BookSalesEntity> findBySalesCode(String salesCode);
    Page<BookSalesEntity> findByBookId(Long bookId, Pageable pageable);
    Page<BookSalesEntity> findByPriceGreaterThanOrderByPriceDesc(Double price, Pageable pageable);
    Page<BookSalesEntity> findByIsAvailableTrue(Pageable pageable);
    Page<BookSalesEntity> findByDiscountGreaterThan(Double discount, Pageable pageable);
}