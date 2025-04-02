package com.batubook.backend.entity;

import com.batubook.backend.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {
        "userProfile", "reviews", "quotes", "sentMessages", "receivedMessages",
        "followers", "followingUsers", "bookInteractions", "repostSaves"
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username cannot be empty or contain only spaces.")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16 characters.")
    @Pattern(regexp = "^\\S.*\\S$", message = "Username cannot have leading or trailing spaces.")
    private String username;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "User Email cannot be empty or contain only spaces.")
    @Size(min = 8, max = 256, message = "User Email must be between 8 and 256 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email should be valid.")
    @Email(message = "Email should be valid.")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be empty or contain only spaces.")
    @Size(min = 8, max = 256, message = "Password must be between 8 and 256 characters.")
    @Pattern(
            regexp = "^(?!\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\[\\]{};:'\",<>./?~`|]).{8,256}(?<!\\s)$",
            message = "Password must be between 8-256 characters, contain at least one lowercase letter, one uppercase letter, one number, one special character, and cannot start or end with a space."
    )
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfileEntity userProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ReviewEntity> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<QuoteEntity> quotes;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MessageEntity> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MessageEntity> receivedMessages;

    @OneToMany(mappedBy = "followedUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<FollowEntity> followers;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<FollowEntity> followingUsers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<BookInteractionEntity> bookInteractions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<RepostSaveEntity> repostSaves;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.username != null) {
            this.username = this.username.trim().toLowerCase();
        }

        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }

        if (this.password != null) {
            this.password = this.password.trim();
        }
    }
}