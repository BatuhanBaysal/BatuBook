package com.batubook.backend.entity;

import com.batubook.backend.entity.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"likes"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Message Content cannot be empty or just whitespace.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Message Content cannot have leading or trailing spaces.")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UserEntity receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_interaction_id")
    private BookInteractionEntity bookInteraction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private QuoteEntity quote;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LikeEntity> likes;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.messageContent != null) {
            this.messageContent = this.messageContent.trim();
        }
    }
}