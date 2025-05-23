package com.batubook.backend.dto;

import com.batubook.backend.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private String messageContent;
    private Long senderId;
    private Long receiverId;
    private Long interactionId;
    private Long reviewId;
    private Long quoteId;
    private MessageType messageType;
}