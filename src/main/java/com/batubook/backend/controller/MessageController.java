package com.batubook.backend.controller;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.enums.MessageType;
import com.batubook.backend.service.serviceInterface.MessageServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class MessageController {

    private final MessageServiceInterface messageService;

    @PostMapping("/createMessage")
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageDTO messageDTO) {
        log.info("Creating a new message from senderId: {}", messageDTO.getSenderId());
        MessageDTO createdMessage = messageService.createMessage(messageDTO);
        return ResponseEntity.ok(createdMessage);
    }

    @GetMapping("/searchMessageId/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        log.info("Fetching message with ID: {}", id);
        MessageDTO messageDTO = messageService.getMessageById(id);
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/searchAllMessages")
    public ResponseEntity<Page<MessageDTO>> getAllMessages(Pageable pageable) {
        log.info("Fetching all messages with pagination");
        Page<MessageDTO> messages = messageService.getAllMessages(pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/searchMessageType/{messageType}")
    public ResponseEntity<Page<MessageDTO>> getMessagesByType(@PathVariable MessageType messageType, Pageable pageable) {
        log.info("Fetching messages with type: {}", messageType);
        Page<MessageDTO> messages = messageService.getMessageByMessageType(messageType, pageable);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/updateMessage/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Long id, @Valid @RequestBody MessageDTO messageDTO) {
        log.info("Updating message with ID: {}", id);
        MessageDTO updatedMessage = messageService.updateMessage(id, messageDTO);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/deleteMessage/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.info("Deleting message with ID: {}", id);
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}