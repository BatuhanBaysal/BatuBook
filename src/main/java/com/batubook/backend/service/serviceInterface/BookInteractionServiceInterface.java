package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.BookInteractionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookInteractionServiceInterface {

    BookInteractionDTO registerBookInteraction(BookInteractionDTO bookInteractionDTO);
    BookInteractionDTO getBookInteractionById(Long id);
    Page<BookInteractionDTO> getAllBookInteractions(Pageable pageable);
    Page<BookInteractionDTO> getByUserIdAndIsReadTrue(Long userId, Pageable pageable);
    Page<BookInteractionDTO> getByUserIdAndIsLikedTrue(Long userId, Pageable pageable);
    Page<BookInteractionDTO> getByBookIdAndIsReadTrue(Long bookId, Pageable pageable);
    Page<BookInteractionDTO> getByBookIdAndIsLikedTrue(Long bookId, Pageable pageable);
    boolean isBookReadByUser(Long userId, Long bookId);
    boolean isBookLikedByUser(Long userId, Long bookId);
    BookInteractionDTO modifyBookInteraction(Long id, BookInteractionDTO bookInteractionDTO);
    void removeBookInteraction(Long id);
}