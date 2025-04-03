package com.batubook.backend.entity;

import com.batubook.backend.entity.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repostSaves")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepostSaveEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private QuoteEntity quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_interaction_id")
    private BookInteractionEntity bookInteraction;

    @PrePersist
    @PreUpdate
    private void validateOnlyOneReference() {
        int count = 0;
        if (review != null) count++;
        if (quote != null) count++;
        if (bookInteraction != null) count++;

        if (count != 1) {
            throw new IllegalStateException("Only one content type (Review, Quote, or BookInteraction) can be referenced.");
        }
    }
}