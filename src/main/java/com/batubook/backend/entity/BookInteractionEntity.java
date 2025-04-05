package com.batubook.backend.entity;

import com.batubook.backend.entity.validation.ValidBookInteraction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "book_interactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"comments", "likes", "repostSaves"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidBookInteraction
public class BookInteractionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256)
    @Size(max = 256, message = "Description must be at most 256 characters.")
    private String description;

    @Column(nullable = false)
    private Boolean isRead;

    @Column(nullable = false)
    private Boolean isLiked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @OneToMany(mappedBy = "bookInteraction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MessageEntity> comments;

    @OneToMany(mappedBy = "bookInteraction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LikeEntity> likes;

    @OneToMany(mappedBy = "bookInteraction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RepostSaveEntity> repostSaves;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.description != null) {
            this.description = this.description.trim();
        }

        if (!this.getIsRead() && this.getIsLiked()) {
            throw new IllegalStateException("A book cannot be liked if it is not read.");
        }
    }
}