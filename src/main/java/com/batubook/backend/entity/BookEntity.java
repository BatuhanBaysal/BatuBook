package com.batubook.backend.entity;

import com.batubook.backend.entity.enums.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Book Title cannot be null.")
    @Size(min = 2, max = 64, message = "Book Title must be between 2 and 64 characters.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Book Title cannot have leading or trailing spaces.")
    private String title;

    @Column(nullable = false)
    @NotNull(message = "Book Author cannot be null.")
    @Size(min = 2, max = 64, message = "Book Author must be between 2 and 64 characters.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Book Author cannot have leading or trailing spaces.")
    private String author;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Book ISBN cannot be empty.")
    @Size(min = 10, max = 10, message = "Book ISBN must be 10 characters.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Book ISBN cannot have leading or trailing spaces.")
    private String isbn;

    @Column(nullable = false)
    @NotNull(message = "Number of Book Pages cannot be blank.")
    @Min(value = 1, message = "Page count must be at least 1.")
    @Max(value = 5000, message = "Page count must not exceed 5000.")
    private int pageCount;

    @Column(nullable = false)
    @PastOrPresent(message = "Publish date must be today or in the past.")
    private LocalDate publishDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Book Genre cannot be null.")
    private Genre genre;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviews;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuoteEntity> quotes;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.title != null) {
            this.title = this.title.trim();
        }

        if (this.author != null) {
            this.author = this.author.trim();
        }

        if (this.isbn != null) {
            this.isbn = this.isbn.trim();
        }
    }
}