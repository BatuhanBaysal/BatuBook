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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageServiceInterface {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuoteRepository quoteRepository;

    @Override
    @Transactional
    public MessageDTO registerMessage(MessageDTO messageDTO) {
        try {
            logger.info("Creating message for senderId: {}", messageDTO.getSenderId());
            UserEntity sender = validateAndGetSender(messageDTO.getSenderId());
            UserEntity receiver = validateAndGetReceiver(messageDTO.getReceiverId(), messageDTO.getMessageType());

            MessageEntity messageEntity = messageMapper.messageDTOToEntity(messageDTO);
            messageEntity.setSender(sender);
            messageEntity.setReceiver(receiver);

            validateReviewOrQuote(messageDTO, messageEntity);
            messageEntity.setMessageType(messageDTO.getMessageType());
            messageRepository.save(messageEntity);
            logger.info("Message created successfully with ID: {}", messageEntity.getId());
            messageDTO.setId(messageEntity.getId());
            return messageDTO;

        } catch (CustomExceptions.BadRequestException e) {
            logger.error("Bad Request Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating message: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Message could not be created: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDTO getMessageById(Long id) {
        logger.info("Attempting to retrieve message with ID: {}", id);
        MessageEntity messageEntity = messageRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Message not found with ID: {}", id);
                    return new CustomExceptions.NotFoundException("Message not found with ID: " + id);
                });

        logger.info("Successfully retrieved message with ID: {}", id);
        return messageMapper.messageEntityToDTO(messageEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDTO> getAllMessages(Pageable pageable) {
        logger.debug("Fetching all messages with pagination: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<MessageEntity> allMessages = messageRepository.findAll(pageable);
        logger.info("Successfully fetched {} messages", allMessages.getNumberOfElements());
        return allMessages.map(messageMapper::messageEntityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDTO> getMessageByMessageType(MessageType messageType, Pageable pageable) {
        logger.info("Started fetching messages with type: {} and pagination (Page: {}, Size: {})",
                messageType, pageable.getPageNumber(), pageable.getPageSize());
        Page<MessageEntity> messages = messageRepository.findByMessageType(messageType, pageable);
        logger.info("Fetched {} messages with type: {}. Total pages: {}, Total elements: {}",
                messages.getTotalElements(), messageType, messages.getTotalPages(), messages.getTotalElements());
        return messages.map(messageMapper::messageEntityToDTO);
    }

    @Override
    @Transactional
    public MessageDTO modifyMessage(Long id, MessageDTO messageDTO) {
        try {
            logger.info("Updating message with ID: {}", id);
            MessageEntity existingMessage = messageRepository.findById(id)
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Message not found with ID: " + id));

            UserEntity sender = validateAndGetSender(messageDTO.getSenderId());
            UserEntity receiver = validateAndGetReceiver(messageDTO.getReceiverId(), messageDTO.getMessageType());

            updateMessageFields(existingMessage, messageDTO, sender, receiver);
            validateReviewOrQuote(messageDTO, existingMessage);
            messageRepository.save(existingMessage);
            logger.info("Message updated successfully with ID: {}", existingMessage.getId());
            messageDTO.setId(existingMessage.getId());
            return messageDTO;

        } catch (CustomExceptions.NotFoundException e) {
            logger.error("Message not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating message: {}", e.getMessage());
            throw new CustomExceptions.InternalServerErrorException("Message could not be updated: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeMessage(Long id) {
        logger.info("Attempting to remove message with ID: {}", id);
        if (!messageRepository.existsById(id)) {
            logger.error("Message with ID: {} not found for deletion", id);
            throw new CustomExceptions.NotFoundException("Message not found with ID: " + id);
        }

        messageRepository.deleteById(id);
        logger.info("Successfully deleted message with ID: {}", id);
    }

    private UserEntity validateAndGetSender(Long senderId) {
        return userRepository.findById(senderId)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("Sender not found with ID: " + senderId));
    }

    private UserEntity validateAndGetReceiver(Long receiverId, MessageType messageType) {
        if (messageType == MessageType.PERSONAL && receiverId == null) {
            throw new CustomExceptions.BadRequestException("Receiver is required for PERSONAL message type");
        }
        if ((messageType == MessageType.REVIEW || messageType == MessageType.QUOTE) && receiverId != null) {
            throw new CustomExceptions.BadRequestException("Receiver should be null for REVIEW or QUOTE message type");
        }
        if (receiverId != null) {
            return userRepository.findById(receiverId)
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Receiver not found with ID: " + receiverId));
        }
        return null;
    }

    private void validateReviewOrQuote(MessageDTO messageDTO, MessageEntity messageEntity) {
        if (messageDTO.getMessageType() == MessageType.REVIEW && messageDTO.getReviewId() != null) {
            ReviewEntity review = reviewRepository.findById(messageDTO.getReviewId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Review not found with ID: " + messageDTO.getReviewId()));
            messageEntity.setReview(review);
        } else if (messageDTO.getMessageType() == MessageType.QUOTE && messageDTO.getQuoteId() != null) {
            QuoteEntity quote = quoteRepository.findById(messageDTO.getQuoteId())
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("Quote not found with ID: " + messageDTO.getQuoteId()));
            messageEntity.setQuote(quote);
        }
    }

    private void updateMessageFields(MessageEntity existingMessage, MessageDTO messageDTO, UserEntity sender, UserEntity receiver) {
        existingMessage.setSender(sender);
        existingMessage.setReceiver(receiver);
        existingMessage.setMessageContent(messageDTO.getMessageContent());
        existingMessage.setMessageType(messageDTO.getMessageType());
    }
}