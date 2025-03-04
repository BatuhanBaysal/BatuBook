package com.batubook.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Message Content cannot be null.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Message Content cannot have leading or trailing spaces.")
    private String messageContent;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private UserEntity receiver;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.messageContent != null) {
            this.messageContent = this.messageContent.trim();
        }
    }
}