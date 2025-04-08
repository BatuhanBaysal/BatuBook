package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.BookSalesDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.BookSalesEntity;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.BookSalesMapper;
import com.batubook.backend.repository.BookSalesRepository;
import com.batubook.backend.service.serviceInterface.BookSalesServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookSalesServiceImpl implements BookSalesServiceInterface {

    private final BookSalesRepository bookSalesRepository;
    private final BookSalesMapper bookSalesMapper;
    private static final Logger logger = LoggerFactory.getLogger(BookSalesServiceImpl.class);

    @Override
    @Transactional
    public BookSalesDTO registerBookSales(BookSalesDTO bookSalesDTO) {
        logger.info("Creating a new book sales with code: {}", bookSalesDTO.getSalesCode());
        try {
            BookSalesEntity bookSales = bookSalesMapper.bookSalesDTOToEntity(bookSalesDTO);
            logger.debug("Converted BookSalesDTO to BookSalesEntity: {}", bookSales);
            BookSalesEntity savedBookSales = bookSalesRepository.save(bookSales);
            logger.info("Book Sales saved successfully with ID: {}", savedBookSales.getId());
            return bookSalesMapper.bookSalesEntityToDTO(savedBookSales);

        } catch (Exception e) {
            logger.error("Error occurred while creating the book sales: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while creating the book sales", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookSalesDTO getBookSalesById(Long id) {
        logger.info("Attempting to retrieve book sales with ID: {}", id);
        BookSalesEntity bookSalesEntity = bookSalesRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Book sales not found with ID: {}", id);
                    return new EntityNotFoundException("Book Sales with ID " + id + " not found");
                });

        logger.info("Successfully retrieved book sales with ID: {}", id);
        return bookSalesMapper.bookSalesEntityToDTO(bookSalesEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSalesDTO> getAllBookSales(Pageable pageable) {
        logger.debug("Fetching all book sales with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesEntity> allBookSales = bookSalesRepository.findAll(pageable);
        logger.info("Successfully fetched {} book sales", allBookSales.getNumberOfElements());
        return allBookSales.map(bookSalesMapper::bookSalesEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BookSalesDTO getBookSalesBySalesCode(String salesCode) {
        logger.info("Searching for book sales with code: '{}'", salesCode);
        BookSalesEntity bookSalesEntity = bookSalesRepository.findBySalesCode(salesCode)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("Book sales not found with sales code: " + salesCode));
        logger.info("Found book sales with code: '{}'", salesCode);
        return bookSalesMapper.bookSalesEntityToDTO(bookSalesEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSalesDTO> getBookSalesByBookId(Long bookId, Pageable pageable) {
        logger.info("Fetching book sales for book ID: {} with pagination (Page: {}, Size: {})",
                bookId, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesEntity> bookSalesPage = bookSalesRepository.findByBookId(bookId, pageable);
        logger.info("Fetched {} book sales for book ID: {}", bookSalesPage.getTotalElements(), bookId);
        return bookSalesPage.map(bookSalesMapper::bookSalesEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSalesDTO> getBookSalesByPriceGreaterThanOrderByPriceDesc(Double price, Pageable pageable) {
        logger.info("Fetching book sales with price greater than: {} ordered by price descending with pagination (Page: {}, Size: {})",
                price, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesEntity> bookSalesPage = bookSalesRepository.findByPriceGreaterThanOrderByPriceDesc(price, pageable);
        logger.info("Fetched {} book sales with price greater than: {} ordered by price descending",
                bookSalesPage.getTotalElements(), price);
        return bookSalesPage.map(bookSalesMapper::bookSalesEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSalesDTO> getBookSalesByIsAvailableTrue(Pageable pageable) {
        logger.info("Fetching available book sales with pagination (Page: {}, Size: {})",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesEntity> bookSalesPage = bookSalesRepository.findByIsAvailableTrue(pageable);
        logger.info("Fetched {} available book sales", bookSalesPage.getTotalElements());
        return bookSalesPage.map(bookSalesMapper::bookSalesEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSalesDTO> getBookSalesByDiscountGreaterThan(Double discount, Pageable pageable) {
        logger.info("Fetching book sales with discount greater than: {} with pagination (Page: {}, Size: {})",
                discount, pageable.getPageNumber(), pageable.getPageSize());
        Page<BookSalesEntity> bookSalesPage = bookSalesRepository.findByDiscountGreaterThan(discount, pageable);
        logger.info("Fetched {} book sales with discount greater than: {}", bookSalesPage.getTotalElements(), discount);
        return bookSalesPage.map(bookSalesMapper::bookSalesEntityToDTO);
    }

    @Override
    @Transactional
    public BookSalesDTO modifyBookSales(Long id, BookSalesDTO bookSalesDTO) {
        logger.info("Attempting to update book sales with id: {}", id);
        try {
            BookSalesEntity existingBookSales = bookSalesRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book sales not found with id: " + id));

            logger.info("Updating book sales with id: {}. New details: SalesCode = '{}', Publisher= '{}', Price = '{}', Stock Quantity = '{}', Currency = '{}', Discount = '{}', BookId = '{}', Available = '{}'",
                    id, bookSalesDTO.getSalesCode(), bookSalesDTO.getPublisher(), bookSalesDTO.getPrice(), bookSalesDTO.getStockQuantity(), bookSalesDTO.getCurrency(), bookSalesDTO.getDiscount(), bookSalesDTO.getBookId(), bookSalesDTO.getIsAvailable());

            updateBookSalesDetails(existingBookSales, bookSalesDTO);
            BookSalesEntity updatedBookSales = bookSalesRepository.save(existingBookSales);
            logger.info("Successfully updated book sales with id: {}", id);
            return bookSalesMapper.bookSalesEntityToDTO(updatedBookSales);

        } catch (Exception e) {
            logger.error("Error occurred while updating book sales with id: {}", id, e);
            throw new RuntimeException("Error occurred while updating the book sales", e);
        }
    }

    @Override
    @Transactional
    public void removeBookSales(Long id) {
        logger.info("Attempting to remove book sales with ID: {}", id);
        if (!bookSalesRepository.existsById(id)) {
            logger.error("Book sales with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Book sales not found with ID: " + id);
        }

        bookSalesRepository.deleteById(id);
        logger.info("Successfully deleted book sales with ID: {}", id);
    }

    private void updateBookSalesDetails(BookSalesEntity bookSalesEntity, BookSalesDTO bookSalesDTO) {
        if (bookSalesDTO.getSalesCode() != null) {
            bookSalesEntity.setSalesCode(bookSalesDTO.getSalesCode());
        }
        if (bookSalesDTO.getPublisher() != null) {
            bookSalesEntity.setPublisher(bookSalesDTO.getPublisher());
        }
        if (bookSalesDTO.getPrice() != null) {
            bookSalesEntity.setPrice(bookSalesDTO.getPrice());
        }
        if (bookSalesDTO.getStockQuantity() != null) {
            bookSalesEntity.setStockQuantity(bookSalesDTO.getStockQuantity());
        }
        if (bookSalesDTO.getCurrency() != null) {
            bookSalesEntity.setCurrency(bookSalesDTO.getCurrency());
        }
        if (bookSalesDTO.getDiscount() != null) {
            bookSalesEntity.setDiscount(bookSalesDTO.getDiscount());
        }
        if (bookSalesDTO.getBookId() != null) {
            BookEntity bookEntity = new BookEntity();
            bookEntity.setId(bookSalesDTO.getBookId());
            bookSalesEntity.setBook(bookEntity);
        }
        bookSalesEntity.setIsAvailable(bookSalesDTO.getIsAvailable());
    }
}