package com.batubook.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_interaction_id")
    private BookInteractionEntity bookInteraction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private QuoteEntity quote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private MessageEntity message;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        int filledCount = 0;

        if (bookInteraction != null) filledCount++;
        if (review != null) filledCount++;
        if (quote != null) filledCount++;
        if (message != null) filledCount++;

        if (filledCount != 1) {
            throw new IllegalStateException("Only one type of like can be selected.");
        }
    }
}