package com.batubook.backend.dto;

import com.batubook.backend.entity.BookSalesEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSalesDTO {

    private Long id;
    private String salesCode;
    private String publisher;
    private Double price;
    private Integer stockQuantity;
    private BookSalesEntity.Currency currency;
    private Double discount;
    private boolean isAvailable;
    private Long bookId;
}