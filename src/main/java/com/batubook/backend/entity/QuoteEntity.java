package com.batubook.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "quotes")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"likes", "repostSaves"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Quote text cannot be null or empty.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Quote text cannot have leading or trailing spaces.")
    private String quoteText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LikeEntity> likes;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RepostSaveEntity> repostSaves;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.quoteText != null) {
            this.quoteText = this.quoteText.trim();
        }
    }
}