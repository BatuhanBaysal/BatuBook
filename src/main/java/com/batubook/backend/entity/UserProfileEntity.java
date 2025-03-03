package com.batubook.backend.entity;

import com.batubook.backend.entity.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "User Date Of Birth cannot be null.")
    @Past(message = "User Date Of Birth must be in the past.")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "User Gender cannot be null.")
    private Gender gender;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "User Biography cannot be empty.")
    @Size(min = 1, max = 128, message = "User Biography must be between 1 and 128 characters.")
    private String biography;

    @Column(nullable = false)
    @NotBlank(message = "User Location cannot be empty.")
    @Size(min = 2, max = 64, message = "User Location information must be between 2 and 64 characters.")
    private String location;

    @Size(min = 2, max = 64, message = "User occupation must be between 2 and 64 characters.")
    private String occupation;

    @Size(min = 2, max = 64, message = "User Education information must be between 2 and 64 characters.")
    private String education;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @PrePersist
    @PreUpdate
    public void preProcess() {
        if (this.location != null) {
            this.location = this.location.trim();
        }

        if (this.occupation != null) {
            this.occupation = this.occupation.trim();
        }

        if (this.education != null) {
            this.education = this.education.trim();
        }
    }
}