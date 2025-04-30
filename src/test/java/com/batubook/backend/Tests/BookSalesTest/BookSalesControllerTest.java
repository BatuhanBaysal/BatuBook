package com.batubook.backend.Tests.BookSalesTest;

import com.batubook.backend.dto.BookDTO;
import com.batubook.backend.dto.BookSalesDTO;
import com.batubook.backend.entity.BookEntity;
import com.batubook.backend.entity.BookSalesEntity;
import com.batubook.backend.entity.enums.Genre;
import com.batubook.backend.repository.BookRepository;
import com.batubook.backend.repository.BookSalesRepository;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookSalesControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(BookSalesControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookSalesRepository bookSalesRepository;

    @Autowired
    private BookRepository bookRepository;

    @BeforeAll
    public static void beforeAll() {
        logger.info("Starting all Book Sales Controller tests...");
    }

    @BeforeEach
    public void setUp() {
        logger.info("Initializing test data for Book Sales Controller...");
        bookRepository.deleteAll();
        bookSalesRepository.deleteAll();
        initializeMockData();
        logger.info("Book Sales Controller test data initialized.");
    }

    @AfterEach
    public void tearDown() {
        logger.info("Book Sales Controller test completed.");
    }

    @AfterAll
    public static void afterAll() {
        logger.info("All Book Sales Controller tests completed.");
    }

    @Order(1)
    @Test
    void testCreateBookSales_Success() throws Exception {
        BookSalesDTO requestDTO = new BookSalesDTO();
        requestDTO.setSalesCode("2222222");
        requestDTO.setPublisher("AnotherPublisher");
        requestDTO.setPrice(30.0);
        requestDTO.setStockQuantity(10);
        requestDTO.setCurrency(BookSalesEntity.Currency.EUR);
        requestDTO.setDiscount(5.0);
        requestDTO.setIsAvailable(true);
        requestDTO.setBookId(bookRepository.findAll().get(0).getId());

        mockMvc.perform(post("/api/book-sales/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.salesCode").value("2222222"));
    }

    @Order(2)
    @Test
    void testCreateBookSales_InvalidData() throws Exception {
        BookSalesDTO invalidDto = new BookSalesDTO(); // boş DTO

        mockMvc.perform(post("/api/book-sales/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Order(3)
    @Test
    void testGetBookSalesById_Success() throws Exception {
        BookSalesEntity entity = bookSalesRepository.findAll().get(0);

        mockMvc.perform(get("/api/book-sales/" + entity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entity.getId()));
    }

    @Order(4)
    @Test
    void testGetBookSalesById_NotFound() throws Exception {
        mockMvc.perform(get("/api/book-sales/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Book Sales with ID 999999 not found"));
    }

    @Order(5)
    @Test
    void testFetchAllBookSales_WithData() throws Exception {
        mockMvc.perform(get("/api/book-sales")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(greaterThan(0)));
    }

    @Order(6)
    @Test
    void testFetchAllBookSales_Empty() throws Exception {
        bookSalesRepository.deleteAll();

        mockMvc.perform(get("/api/book-sales")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Order(7)
    @Test
    void testFetchBookSalesBySalesCode_Success() throws Exception {
        BookSalesEntity entity = bookSalesRepository.findAll().get(0);

        mockMvc.perform(get("/api/book-sales/salesCode/" + entity.getSalesCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesCode").value(entity.getSalesCode()));
    }

    @Order(8)
    @Test
    void testFetchBookSalesBySalesCode_NotFound() throws Exception {
        mockMvc.perform(get("/api/book-sales/salesCode/NOTEXIST"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book sales not found with sales code: NOTEXIST"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.details[0]").value("The requested resource was not found."));
    }

    @Order(9)
    @Test
    void testFetchBookSalesByBookId_Success() throws Exception {
        BookSalesEntity entity = bookSalesRepository.findAll().get(0);

        mockMvc.perform(get("/api/book-sales/bookId/" + entity.getBook().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookId").value(entity.getBook().getId()));
    }

    @Order(10)
    @Test
    void testFetchBookSalesByBookId_NotFound() throws Exception {
        mockMvc.perform(get("/api/book-sales/bookId/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }


    @Order(11)
    @Test
    void testFetchBookSalesByPriceGreaterThan_Success() throws Exception {
        mockMvc.perform(get("/api/book-sales/priceGreaterThan/10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Order(12)
    @Test
    void testFetchBookSalesByPriceGreaterThan_InvalidParam() throws Exception {
        mockMvc.perform(get("/api/book-sales/priceGreaterThan")
                        .param("price", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Order(13)
    @Test
    void testFetchBookSalesByDiscountGreaterThan_Success() throws Exception {
        mockMvc.perform(get("/api/book-sales/discountGreaterThan/0.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Order(14)
    @Test
    void testFetchBookSalesByDiscountGreaterThan_InvalidParam() throws Exception {
        mockMvc.perform(get("/api/book-sales/discountGreaterThan")
                        .param("discount", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Order(15)
    @Test
    void testFetchAvailableBookSales_Success() throws Exception {
        mockMvc.perform(get("/api/book-sales/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Order(16)
    @Test
    void testUpdateBookSales_Success() throws Exception {
        BookSalesEntity entity = bookSalesRepository.findAll().get(0);
        BookSalesDTO dto = new BookSalesDTO();
        dto.setSalesCode("UPDATED123");
        dto.setPublisher("UpdatedPublisher");
        dto.setPrice(45.5);
        dto.setStockQuantity(5);
        dto.setCurrency(BookSalesEntity.Currency.EUR);
        dto.setDiscount(2.0);
        dto.setIsAvailable(false);
        dto.setBookId(entity.getBook().getId());

        mockMvc.perform(put("/api/book-sales/update/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesCode").value("UPDATED123"));
    }

    @Order(17)
    @Test
    void testUpdateBookSales_NotFound() throws Exception {
        BookSalesDTO dto = new BookSalesDTO();
        dto.setSalesCode("XXX");
        dto.setPublisher("None");
        dto.setPrice(0.0);
        dto.setStockQuantity(0);
        dto.setCurrency(BookSalesEntity.Currency.USD);
        dto.setDiscount(0.0);
        dto.setIsAvailable(false);
        dto.setBookId(1L);

        mockMvc.perform(put("/api/book-sales/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error occurred while updating the book sales"));
    }

    @Order(18)
    @Test
    void testDeleteBookSales_Success() throws Exception {
        BookSalesEntity entity = bookSalesRepository.findAll().get(0);

        mockMvc.perform(delete("/api/book-sales/delete/" + entity.getId()))
                .andExpect(status().isNoContent());
    }

    @Order(19)
    @Test
    void testDeleteBookSales_NotFound() throws Exception {
        mockMvc.perform(delete("/api/book-sales/delete/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book sales not found with ID: 999999"))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.details").isArray());
    }

    private void initializeMockData() {
        BookEntity mockBookEntity = createMockBook();

        BookSalesDTO mockSalesDTO = new BookSalesDTO();
        mockSalesDTO.setSalesCode("1111111");
        mockSalesDTO.setPublisher("TestPublisher");
        mockSalesDTO.setPrice(20.5);
        mockSalesDTO.setStockQuantity(5);
        mockSalesDTO.setCurrency(BookSalesEntity.Currency.USD);
        mockSalesDTO.setDiscount(2.5);
        mockSalesDTO.setIsAvailable(true);
        mockSalesDTO.setBookId(mockBookEntity.getId());

        BookSalesEntity salesEntity = new BookSalesEntity();
        salesEntity.setSalesCode(mockSalesDTO.getSalesCode());
        salesEntity.setPublisher(mockSalesDTO.getPublisher());
        salesEntity.setPrice(mockSalesDTO.getPrice());
        salesEntity.setStockQuantity(mockSalesDTO.getStockQuantity());
        salesEntity.setCurrency(mockSalesDTO.getCurrency());
        salesEntity.setDiscount(mockSalesDTO.getDiscount());
        salesEntity.setIsAvailable(mockSalesDTO.getIsAvailable());
        salesEntity.setBook(mockBookEntity);

        bookSalesRepository.save(salesEntity);
    }

    private BookEntity createMockBook() {
        BookDTO mockBookDTO = new BookDTO();
        mockBookDTO.setTitle("1984");
        mockBookDTO.setAuthor("George Orwell");
        mockBookDTO.setIsbn("1111111111");
        mockBookDTO.setPageCount(352);
        mockBookDTO.setPublishDate("1949-06-08");
        mockBookDTO.setGenre(Genre.DYSTOPIA);

        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(mockBookDTO.getTitle());
        bookEntity.setAuthor(mockBookDTO.getAuthor());
        bookEntity.setIsbn(mockBookDTO.getIsbn());
        bookEntity.setPageCount(mockBookDTO.getPageCount());
        bookEntity.setPublishDate(LocalDate.parse(mockBookDTO.getPublishDate()));
        bookEntity.setGenre(mockBookDTO.getGenre());

        return bookRepository.save(bookEntity);
    }
}