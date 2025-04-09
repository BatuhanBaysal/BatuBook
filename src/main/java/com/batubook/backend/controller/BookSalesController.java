package com.batubook.backend.controller;

import com.batubook.backend.dto.BookSalesDTO;
import com.batubook.backend.service.serviceImplementation.BookSalesServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/book-sales")
@RequiredArgsConstructor
public class BookSalesController {

    private final BookSalesServiceImpl bookSalesService;
    private static final Logger logger = LoggerFactory.getLogger(BookSalesController.class);

    @PostMapping("/create")
    public ResponseEntity<BookSalesDTO> createBookSales(@Valid @RequestBody BookSalesDTO bookSalesDTO) {
        logger.info("Attempting to create new book sales: {}", bookSalesDTO);
        BookSalesDTO createBookSales = bookSalesService.registerBookSales(bookSalesDTO);
        logger.info("Successfully created book sales with id: {}", createBookSales.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createBookSales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookSalesDTO> fetchBookSalesById(@PathVariable Long id) {
        logger.info("Received GET request for /api/book-sales/{}", id);
        BookSalesDTO bookSalesDTO = bookSalesService.getBookSalesById(id);
        logger.info("Returned response for book sales with ID: {}", id);
        return ResponseEntity.ok(bookSalesDTO);
    }

    @GetMapping
    public ResponseEntity<Page<BookSalesDTO>> fetchAllBookSales(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/book-sales called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesDTO> allBookSales = bookSalesService.getAllBookSales(pageable);
        logger.info("Successfully fetched {} book sales", allBookSales.getNumberOfElements());
        return ResponseEntity.ok(allBookSales);
    }

    @GetMapping("/salesCode/{salesCode}")
    public ResponseEntity<BookSalesDTO> fetchBookSalesBySalesCode(@PathVariable String salesCode) {
        logger.info("Fetching book sales for sales code: {}", salesCode);
        BookSalesDTO bookSalesDTO = bookSalesService.getBookSalesBySalesCode(salesCode);
        logger.info("Successfully retrieved book sales with sales code: {}", salesCode);
        return ResponseEntity.ok(bookSalesDTO);
    }

    @GetMapping("/bookId/{bookId}")
    public ResponseEntity<Page<BookSalesDTO>> fetchBookSalesByBookId(
            @PathVariable Long bookId,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch book sales for book ID: {} with pagination (Page: {}, Size: {})",
                bookId, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesDTO> bookSalesByBookId = bookSalesService.getBookSalesByBookId(bookId, pageable);
        logger.info("Successfully fetched {} book sales for book ID: {}", bookSalesByBookId.getTotalElements(), bookId);
        return ResponseEntity.ok(bookSalesByBookId);
    }

    @GetMapping("/priceGreaterThan/{price}")
    public ResponseEntity<Page<BookSalesDTO>> fetchBookSalesByPriceGreaterThan(
            @PathVariable Double price,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch book sales with price greater than: {} with pagination (Page: {}, Size: {})",
                price, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesDTO> bookSalesByPrice = bookSalesService.getBookSalesByPriceGreaterThanOrderByPriceDesc(price, pageable);
        logger.info("Successfully fetched {} book sales with price greater than: {}", bookSalesByPrice.getTotalElements(), price);
        return ResponseEntity.ok(bookSalesByPrice);
    }

    @GetMapping("/discountGreaterThan/{discount}")
    public ResponseEntity<Page<BookSalesDTO>> fetchBookSalesByDiscountGreaterThan(
            @PathVariable Double discount,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch book sales with discount greater than: {} with pagination (Page: {}, Size: {})",
                discount, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesDTO> bookSalesByDiscount = bookSalesService.getBookSalesByDiscountGreaterThan(discount, pageable);
        logger.info("Successfully fetched {} book sales with discount greater than: {}",
                bookSalesByDiscount.getTotalElements(), discount);
        return ResponseEntity.ok(bookSalesByDiscount);
    }

    @GetMapping("/available")
    public ResponseEntity<Page<BookSalesDTO>> fetchAvailableBookSales(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("Received request to fetch available book sales with pagination (Page: {}, Size: {})",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesDTO> availableBookSales = bookSalesService.getBookSalesByIsAvailableTrue(pageable);
        logger.info("Successfully fetched {} book sales available", availableBookSales.getTotalElements());
        return ResponseEntity.ok(availableBookSales);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BookSalesDTO> updateBookSales(@PathVariable Long id, @Valid @RequestBody BookSalesDTO bookSalesDTO) {
        logger.info("Updating book sales with ID: {}", id);
        BookSalesDTO updatedBookSales = bookSalesService.modifyBookSales(id, bookSalesDTO);
        logger.info("Successfully updated message with ID: {}. Updated details: {}", id, updatedBookSales);
        return ResponseEntity.ok(updatedBookSales);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteBookSales(@PathVariable Long id) {
        logger.info("Received request to delete book sales with ID: {}", id);
        bookSalesService.removeBookSales(id);
        logger.info("Successfully deleted book sales with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}