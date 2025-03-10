package com.batubook.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private UserProfileDTO userProfile;
    private Set<ReviewDTO> reviews;
    private Set<QuoteDTO> quotes;
    private Set<MessageDTO> sentMessages;
    private Set<MessageDTO> receivedMessages;
}