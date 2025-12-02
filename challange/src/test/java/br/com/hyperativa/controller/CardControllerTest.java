package br.com.hyperativa.controller;

import br.com.hyperativa.service.CardService;
import br.com.hyperativa.service.TxtParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardControllerTest {

    @InjectMocks
    private CardController controller;

    @Mock
    private CardService cardService;

    @Mock
    private TxtParserService parser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addOne_shouldReturn201_whenCardValid() {
        String cardNumber = "1234567890123";
        String generatedId = "abc-123";

        when(cardService.storeCard(cardNumber, "manual")).thenReturn(generatedId);

        ResponseEntity<?> response = controller.addOne(Map.of("cardNumber", cardNumber));

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(Map.of("id", generatedId), response.getBody());
        verify(cardService).storeCard(cardNumber, "manual");
    }

    @Test
    void addOne_shouldReturn400_whenCardInvalid() {
        ResponseEntity<?> response = controller.addOne(Map.of("cardNumber", "abc123"));

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(Map.of("error", "invalid_card"), response.getBody());
        verify(cardService, never()).storeCard(anyString(), anyString());
    }

    @Test
    void exists_shouldReturnTrue_whenCardExists() {
        String cardNumber = "1234567890123";
        String cardId = "abc-123";

        when(cardService.exists(cardNumber)).thenReturn(Optional.of(cardId));

        ResponseEntity<?> response = controller.exists(Map.of("cardNumber", cardNumber));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Map.of("exists", true, "id", cardId), response.getBody());
    }

    @Test
    void exists_shouldReturnFalse_whenCardInvalid() {
        ResponseEntity<?> response = controller.exists(Map.of("cardNumber", "invalid"));

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(Map.of("exists", false), response.getBody());
    }

    @Test
    void uploadFile_shouldReturnOk_withResults() throws Exception {
        String content = "DESAFIO-HYPERATIVA\n1234567890123";
        MockMultipartFile file = new MockMultipartFile(
                "file", "cards.txt", "text/plain", content.getBytes()
        );

        TxtParserService.ParseResult parseResult = new TxtParserService.ParseResult();
        parseResult.cards.add("1234567890123");
        parseResult.rejected.add("ln 2 invalid card");

        when(parser.parse(any(ByteArrayInputStream.class))).thenReturn(parseResult);
        when(cardService.storeBatch(parseResult.cards, parseResult.rejected, "file_upload"))
                .thenReturn(Map.of("inserted", 1, "rejected", parseResult.rejected));

        ResponseEntity<?> response = controller.uploadFile(file);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Map.of("inserted", 1, "rejected", parseResult.rejected), response.getBody());

        verify(parser).parse(any(ByteArrayInputStream.class));
        verify(cardService).storeBatch(parseResult.cards, parseResult.rejected, "file_upload");
    }
}
