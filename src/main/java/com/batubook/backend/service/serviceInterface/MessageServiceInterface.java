package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageServiceInterface {

    MessageDTO registerMessage(MessageDTO messageDTO);
    MessageDTO getMessageById(Long id);
    Page<MessageDTO> getAllMessages(Pageable pageable);
    Page<MessageDTO> getMessageByMessageType(MessageType messageType, Pageable pageable);
    MessageDTO modifyMessage(Long id, MessageDTO messageDTO);
    void removeMessage(Long id);
}