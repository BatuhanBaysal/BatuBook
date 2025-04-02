package com.batubook.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "book_sales")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSalesEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Book Sales Code cannot be empty.")
    @Size(min = 7, max = 7, message = "Book Sales Code must be 7 characters.")
    private String salesCode;

    @Column(nullable = false)
    @NotBlank(message = "Publisher name cannot be empty.")
    @Size(min = 2, max = 64, message = "Publisher name must be between 2 and 64 characters.")
    private String publisher;

    @Column(nullable = false)
    @NotNull(message = "Book Sales price cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    private Double price;

    @Column(nullable = false)
    @NotNull(message = "Stock quantity cannot be null.")
    @Min(value = 0, message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    @NotNull(message = "Currency cannot be null.")
    private Currency currency;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative.")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%.")
    private Double discount;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    public enum Currency {
        TRY, USD, EUR
    }

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.salesCode != null) {
            this.salesCode = this.salesCode.trim();
        }

        if (this.publisher != null) {
            this.publisher = this.publisher.trim();
        }
    }
}