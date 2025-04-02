package com.batubook.backend.entity;

import com.batubook.backend.entity.validation.ValidRating;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "reviews")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"likes", "repostSaves"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Review text cannot be blank.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Review text cannot have leading or trailing spaces.")
    private String reviewText;

    @Column(nullable = false)
    @NotNull(message = "Review Rating cannot be null.")
    @ValidRating
    private BigDecimal rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LikeEntity> likes;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RepostSaveEntity> repostSaves;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.reviewText != null) {
            this.reviewText = this.reviewText.trim();
        }
    }
}