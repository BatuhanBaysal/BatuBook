package com.batubook.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "reviews")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Review text cannot be null.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Review text cannot have leading or trailing spaces.")
    private String reviewText;

    @Column(nullable = false)
    @NotNull(message = "Review Rating cannot be null.")
    @DecimalMin(value = "1.0", message = "Review Rating must be at least 1.")
    @DecimalMax(value = "5.0", message = "Review Rating must not exceed 5.")
    @Pattern(regexp = "^([1-5](\\.5)?)$", message = "Review Rating must be a whole number or 0.5 steps between 1 and 5.")
    private BigDecimal rating;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.reviewText != null) {
            this.reviewText = this.reviewText.trim();
        }
    }
}