package com.batubook.backend.service.serviceImplementation;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.MessageEntity;
import com.batubook.backend.entity.QuoteEntity;
import com.batubook.backend.entity.ReviewEntity;
import com.batubook.backend.entity.UserEntity;
import com.batubook.backend.entity.enums.MessageType;
import com.batubook.backend.exception.CustomExceptions;
import com.batubook.backend.mapper.MessageMapper;
import com.batubook.backend.repository.MessageRepository;
import com.batubook.backend.repository.QuoteRepository;
import com.batubook.backend.repository.ReviewRepository;
import com.batubook.backend.repository.UserRepository;
import com.batubook.backend.service.serviceInterface.MessageServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageServiceInterface {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuoteRepository quoteRepository;
    private final MessageMapper messageMapper;

    @Transactional
    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        try {
            log.info("Creating message for senderId: {}", messageDTO.getSenderId());
            UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Sender not found with ID: " + messageDTO.getSenderId()));

            UserEntity receiver = null;
            if (messageDTO.getReceiverId() != null) {
                receiver = userRepository.findById(messageDTO.getReceiverId())
                        .orElseThrow(() -> new CustomExceptions.NotFoundException("Receiver not found with ID: " + messageDTO.getReceiverId()));
            }

            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setSender(sender);
            messageEntity.setReceiver(receiver);
            messageEntity.setMessageContent(messageDTO.getMessageContent());

            if (messageDTO.getMessageType() == MessageType.REVIEW && messageDTO.getReviewId() != null) {
                ReviewEntity review = reviewRepository.findById(messageDTO.getReviewId())
                        .orElseThrow(() -> new CustomExceptions.NotFoundException("Review not found with ID: " + messageDTO.getReviewId()));
                messageEntity.setReview(review);
            } else if (messageDTO.getMessageType() == MessageType.QUOTE && messageDTO.getQuoteId() != null) {
                QuoteEntity quote = quoteRepository.findById(messageDTO.getQuoteId())
                        .orElseThrow(() -> new CustomExceptions.NotFoundException("Quote not found with ID: " + messageDTO.getQuoteId()));
                messageEntity.setQuote(quote);
            } else if (messageDTO.getMessageType() == MessageType.PERSONAL) {
                messageEntity.setReview(null);
                messageEntity.setQuote(null);
            }

            messageEntity.setMessageType(messageDTO.getMessageType());
            messageRepository.save(messageEntity);
            log.info("Message created successfully with ID: {}", messageEntity.getId());
            messageDTO.setId(messageEntity.getId());
            return messageDTO;

        } catch (Exception e) {
            log.error("Error while creating message: {}", e.getMessage());
            throw new CustomExceptions.BadRequestException("Message could not be created: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public MessageDTO getMessageById(Long id) {
        log.info("Fetching message with ID: {}", id);
        return messageRepository.findById(id)
                .map(messageMapper::messageEntityToMessageDTO)
                .orElseThrow(() -> {
                    log.error("Message not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("Message not found with ID: " + id);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MessageDTO> getAllMessages(Pageable pageable) {
        log.info("Fetching all messages");
        return messageRepository.findAll(pageable).map(messageMapper::messageEntityToMessageDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MessageDTO> getMessageByMessageType(MessageType messageType, Pageable pageable) {
        log.info("Fetching messages with type: {}", messageType);
        return messageRepository.findByMessageType(messageType, pageable)
                .map(messageMapper::messageEntityToMessageDTO);
    }

    @Transactional
    @Override
    public MessageDTO updateMessage(Long id, MessageDTO messageDTO) {
        try {
            log.info("Updating message with ID: {}", id);
            MessageEntity existingMessage = messageRepository.findById(id)
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Message not found with ID: " + id));

            UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Sender not found with ID: " + messageDTO.getSenderId()));

            UserEntity receiver = null;
            if (messageDTO.getReceiverId() != null) {
                receiver = userRepository.findById(messageDTO.getReceiverId())
                        .orElseThrow(() -> new CustomExceptions.NotFoundException("Receiver not found with ID: " + messageDTO.getReceiverId()));
            }

            existingMessage.setSender(sender);
            existingMessage.setReceiver(receiver);
            existingMessage.setMessageContent(messageDTO.getMessageContent());

            if (messageDTO.getMessageType() == MessageType.REVIEW && messageDTO.getReviewId() != null) {
                ReviewEntity review = reviewRepository.findById(messageDTO.getReviewId())
                        .orElseThrow(() -> new CustomExceptions.NotFoundException("Review not found with ID: " + messageDTO.getReviewId()));
                existingMessage.setReview(review);
                existingMessage.setQuote(null);
            } else if (messageDTO.getMessageType() == MessageType.QUOTE && messageDTO.getQuoteId() != null) {
                QuoteEntity quote = quoteRepository.findById(messageDTO.getQuoteId())
                        .orElseThrow(() -> new CustomExceptions.NotFoundException("Quote not found with ID: " + messageDTO.getQuoteId()));
                existingMessage.setQuote(quote);
                existingMessage.setReview(null);
            } else if (messageDTO.getMessageType() == MessageType.PERSONAL) {
                existingMessage.setReview(null);
                existingMessage.setQuote(null);
            }

            existingMessage.setMessageType(messageDTO.getMessageType());
            messageRepository.save(existingMessage);
            log.info("Message updated successfully with ID: {}", existingMessage.getId());
            messageDTO.setId(existingMessage.getId());
            return messageDTO;
        } catch (Exception e) {
            log.error("Error while updating message: {}", e.getMessage());
            throw new CustomExceptions.BadRequestException("Message could not be updated: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public void deleteMessage(Long id) {
        log.info("Deleting message with ID: {}", id);
        try {
            if (!messageRepository.existsById(id)) {
                throw new CustomExceptions.NotFoundException("Message not found with ID: " + id);
            }

            messageRepository.deleteById(id);
            log.info("Message deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error while deleting message with ID {}: {}", id, e.getMessage(), e);
            throw new CustomExceptions.InternalServerErrorException("Message deletion failed: " + e.getMessage());
        }
    }
}