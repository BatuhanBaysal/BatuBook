package com.batubook.backend.Tests.MessageTests;

import com.batubook.backend.dto.MessageDTO;
import com.batubook.backend.entity.*;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.entity.enums.MessageType;
import com.batubook.backend.entity.enums.Role;
import com.batubook.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MessageControllerTest.class);

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookInteractionRepository bookInteractionRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private QuoteRepository quoteRepository;
    @Autowired private MessageRepository messageRepository;

    private UserEntity sender;
    private UserEntity receiver;
    private BookEntity book;
    private BookInteractionEntity interaction;
    private ReviewEntity review;
    private QuoteEntity quote;

    @BeforeEach
    void setUp() {
        sender = userRepository.save(UserEntity.builder().username("sender").email("sender@example.com").password("Sender1234!").role(Role.USER).build());
        receiver = userRepository.save(UserEntity.builder().username("receiver").email("receiver@example.com").password("Receiver1234!").role(Role.USER).build());
        book = bookRepository.save(BookEntity.builder().title("Test Book").author("Test Author").isbn("1234567890").genre(Genre.DYSTOPIA).publishDate(LocalDate.of(1949, 6, 8)).pageCount(352).build());
        interaction = bookInteractionRepository.save(BookInteractionEntity.builder().user(sender).book(book).isRead(true).isLiked(true).description("desc").build());
        review = reviewRepository.save(ReviewEntity.builder().reviewText("great").rating(BigDecimal.valueOf(4.5)).user(sender).book(book).build());
        quote = quoteRepository.save(QuoteEntity.builder().quoteText("quote").user(sender).book(book).build());
    }

    private MessageDTO createMessageDTO(MessageType type) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageContent("Test message");
        dto.setMessageType(type);
        dto.setSenderId(sender.getId());
        switch (type) {
            case PERSONAL -> dto.setReceiverId(receiver.getId());
            case BOOK -> dto.setInteractionId(interaction.getId());
            case REVIEW -> dto.setReviewId(review.getId());
            case QUOTE -> dto.setQuoteId(quote.getId());
        }
        return dto;
    }

    @Test
    @Order(1)
    void create_quote_message_success() throws Exception {
        mockMvc.perform(post("/api/messages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMessageDTO(MessageType.QUOTE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageType").value("quote"));
    }

    @Test
    @Order(2)
    void create_review_message_success() throws Exception {
        mockMvc.perform(post("/api/messages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMessageDTO(MessageType.REVIEW))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageType").value("review"));
    }

    @Test
    @Order(3)
    void create_interaction_message_success() throws Exception {
        mockMvc.perform(post("/api/messages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMessageDTO(MessageType.BOOK))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageType").value("book"));
    }

    @Test
    @Order(4)
    void create_personal_message_success() throws Exception {
        mockMvc.perform(post("/api/messages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMessageDTO(MessageType.PERSONAL))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageType").value("personal"));
    }

    @Test
    @Order(5)
    void create_quote_message_with_receiver_should_fail() throws Exception {
        MessageDTO dto = createMessageDTO(MessageType.QUOTE);
        dto.setReceiverId(receiver.getId());
        mockMvc.perform(post("/api/messages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(6)
    void create_personal_message_without_receiver_should_fail() throws Exception {
        MessageDTO dto = createMessageDTO(MessageType.PERSONAL);
        dto.setReceiverId(null);
        mockMvc.perform(post("/api/messages/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void fetch_message_by_id_success() throws Exception {
        MessageEntity message = messageRepository.save(MessageEntity.builder()
                .messageContent("Test fetch")
                .messageType(MessageType.PERSONAL)
                .sender(sender)
                .receiver(receiver)
                .build());

        mockMvc.perform(get("/api/messages/" + message.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(message.getId()));
    }

    @Test
    @Order(8)
    void fetch_message_by_invalid_id_should_fail() throws Exception {
        mockMvc.perform(get("/api/messages/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    void fetch_all_messages_success() throws Exception {
        messageRepository.save(MessageEntity.builder().messageContent("msg1").messageType(MessageType.PERSONAL).sender(sender).receiver(receiver).build());
        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(10)
    void fetch_messages_by_type_success() throws Exception {
        messageRepository.save(MessageEntity.builder().messageContent("quote msg").messageType(MessageType.QUOTE).sender(sender).quote(quote).build());
        mockMvc.perform(get("/api/messages/search").param("messageType", "QUOTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(11)
    void fetch_messages_by_invalid_type_should_fail() throws Exception {
        mockMvc.perform(get("/api/messages/search").param("messageType", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(12)
    void update_message_success() throws Exception {
        MessageEntity message = messageRepository.save(MessageEntity.builder().messageContent("old msg").messageType(MessageType.QUOTE).sender(sender).quote(quote).build());
        MessageDTO dto = new MessageDTO();
        dto.setMessageContent("Updated msg");
        dto.setMessageType(MessageType.QUOTE);
        dto.setSenderId(sender.getId());
        dto.setQuoteId(quote.getId());

        mockMvc.perform(put("/api/messages/update/" + message.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageContent").value("Updated msg"));
    }

    @Test
    @Order(13)
    void update_message_invalid_id_should_fail() throws Exception {
        MessageDTO dto = createMessageDTO(MessageType.QUOTE);
        mockMvc.perform(put("/api/messages/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    void delete_message_success() throws Exception {
        MessageEntity message = messageRepository.save(MessageEntity.builder().messageContent("to be deleted").messageType(MessageType.QUOTE).sender(sender).quote(quote).build());
        mockMvc.perform(delete("/api/messages/delete/" + message.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(15)
    void delete_message_invalid_id_should_fail() throws Exception {
        mockMvc.perform(delete("/api/messages/delete/999999"))
                .andExpect(status().isNotFound());
    }
}