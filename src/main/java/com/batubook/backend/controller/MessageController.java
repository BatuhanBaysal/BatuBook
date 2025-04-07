package com.batubook.backend.controller;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.enums.MessageType;
import com.batubook.backend.service.serviceInterface.MessageServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageServiceInterface messageService;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @PostMapping("/create")
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageDTO messageDTO) {
        logger.info("Received request to create a new message from senderId: {}", messageDTO.getSenderId());
        MessageDTO createdMessage = messageService.registerMessage(messageDTO);
        logger.info("Successfully created a new message with ID: {}", createdMessage.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> fetchMessageById(@PathVariable Long id) {
        logger.info("Received GET request for /api/messages/{}", id);
        MessageDTO messageDTO = messageService.getMessageById(id);
        logger.info("Returned response for message with ID: {}", id);
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping
    public ResponseEntity<Page<MessageDTO>> fetchAllMessages(@PageableDefault(size = 5) Pageable pageable) {
        logger.info("GET api/messages called with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<MessageDTO> allMessages = messageService.getAllMessages(pageable);
        logger.info("Successfully fetched {} messages", allMessages.getNumberOfElements());
        return ResponseEntity.ok(allMessages);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MessageDTO>> fetchMessagesByType(
            @RequestParam String messageType,
            @PageableDefault(size = 5) Pageable pageable) {
        logger.info("Started fetching messages with type: {}. Page: {}, Size: {}",
                messageType, pageable.getPageNumber(), pageable.getPageSize());
        MessageType messageTypeEnum = MessageType.fromString(messageType);
        Page<MessageDTO> messages = messageService.getMessageByMessageType(messageTypeEnum, pageable);
        logger.info("Successfully fetched {} messages for type: {}. Total pages: {}, Total elements: {}",
                messages.getTotalElements(), messageType, messages.getTotalPages(), messages.getTotalElements());
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Long id, @Valid @RequestBody MessageDTO messageDTO) {
        logger.info("Initiating update for message with ID: {}", id);
        MessageDTO updatedMessage = messageService.modifyMessage(id, messageDTO);
        logger.info("Successfully updated message with ID: {}. Updated details: {}", id, updatedMessage);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        logger.info("Received request to delete message with ID: {}", id);
        messageService.removeMessage(id);
        logger.info("Successfully deleted message with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}