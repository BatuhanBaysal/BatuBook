package com.batubook.backend.repository;

import com.batubook.backend.entity.MessageEntity;
import com.batubook.backend.entity.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    Page<MessageEntity> findByMessageType(MessageType messageType, Pageable pageable);
}