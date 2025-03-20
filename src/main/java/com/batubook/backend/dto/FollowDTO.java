package com.batubook.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDTO {

    private Long id;
    private Long followerId;
    private Long followedUserId;
    private Long followedBookId;
}