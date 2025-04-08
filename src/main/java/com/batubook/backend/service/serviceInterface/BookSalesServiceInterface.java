package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.BookSalesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookSalesServiceInterface {

    BookSalesDTO registerBookSales(BookSalesDTO bookSalesDTO);
    BookSalesDTO getBookSalesById(Long id);
    Page<BookSalesDTO> getAllBookSales(Pageable pageable);
    BookSalesDTO getBookSalesBySalesCode(String salesCode);
    Page<BookSalesDTO> getBookSalesByBookId(Long bookId, Pageable pageable);
    Page<BookSalesDTO> getBookSalesByPriceGreaterThanOrderByPriceDesc(Double price, Pageable pageable);
    Page<BookSalesDTO> getBookSalesByIsAvailableTrue(Pageable pageable);
    Page<BookSalesDTO> getBookSalesByDiscountGreaterThan(Double discount, Pageable pageable);
    BookSalesDTO modifyBookSales(Long id, BookSalesDTO bookSalesDTO);
    void removeBookSales(Long id);
}