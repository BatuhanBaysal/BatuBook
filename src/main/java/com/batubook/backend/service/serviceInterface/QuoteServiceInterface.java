package com.batubook.backend.service.serviceInterface;

import com.batubook.backend.dto.QuoteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteServiceInterface {

    QuoteDTO createQuote(QuoteDTO quoteDTO);
    QuoteDTO getQuoteById(Long id);
    Page<QuoteDTO> getAllQuotes(Pageable pageable);
    QuoteDTO updateQuote(Long id, QuoteDTO quoteDTO);
    void deleteQuote(Long id);
}