package com.batubook.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "quotes")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Quote text cannot be null.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Quote text cannot have leading or trailing spaces.")
    private String quoteText;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.quoteText != null) {
            this.quoteText = this.quoteText.trim();
        }
    }
}