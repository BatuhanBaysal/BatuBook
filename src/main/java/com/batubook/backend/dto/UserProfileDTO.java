package com.batubook.backend.dto;

import com.batubook.backend.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;
    private String profileImageUrl;
    private String dateOfBirth;
    private Gender gender;
    private String biography;
    private String location;
    private String occupation;
    private String education;
    private String interests;
}