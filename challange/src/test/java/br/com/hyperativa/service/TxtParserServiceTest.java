package br.com.hyperativa.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class TxtParserServiceTest {

    private final TxtParserService parser = new TxtParserService();

    @Test
    void parse_shouldAddValidCard() {
        String content = "DESAFIO-TESTE               1234567890123456789012345"; // cartão no formato
        InputStream in = new ByteArrayInputStream(content.getBytes());

        TxtParserService.ParseResult result = parser.parse(in);

        // A linha começa com "DESAFIO" então deve ser ignorada
        assertEquals(0, result.cards.size());
        assertEquals(0, result.rejected.size());
    }

    @Test
    void parse_shouldAddValidCardLine() {
        String line = "C4     4456897000000014";
        InputStream in = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));

        TxtParserService.ParseResult result = parser.parse(in);

        assertEquals(1, result.cards.size());
        assertEquals("4456897000000014", result.cards.get(0));
        assertEquals(0, result.rejected.size());
    }

    @Test
    void parse_shouldRejectInvalidCardLength() {
        // Cartão com menos de 13 dígitos
        String line = "00000001 12345                  ABCDEFGHIJKLMNOPQRSTUVWX";
        InputStream in = new ByteArrayInputStream(line.getBytes());

        TxtParserService.ParseResult result = parser.parse(in);

        assertEquals(0, result.cards.size());
        assertEquals(1, result.rejected.size());
        assertTrue(result.rejected.get(0).contains("invalid card"));
    }

    @Test
    void parse_shouldRejectDigitsOutsideCardArea() {
        // Números na área após o cartão
        String line = "0000000111222333444          12345";
        InputStream in = new ByteArrayInputStream(line.getBytes());

        TxtParserService.ParseResult result = parser.parse(in);

        assertEquals(0, result.cards.size());
        assertEquals(1, result.rejected.size());
        assertTrue(result.rejected.get(0).contains("dígitos fora da área do cartão"));
    }

    @Test
    void parse_shouldPadShortLines() {
        // Linha com menos de 51 caracteres
        String line = "C4     4456897000000014";
        InputStream in = new ByteArrayInputStream(line.getBytes());

        TxtParserService.ParseResult result = parser.parse(in);

        assertEquals(1, result.cards.size());
        assertEquals(0, result.rejected.size());
    }

    @Test
    void parse_shouldHandleParseException() {
        // Linha que força exception na substring
        String line = "C7     445689700000000776576576576";
        InputStream in = new ByteArrayInputStream(line.getBytes());

        TxtParserService.ParseResult result = parser.parse(in);

        assertEquals(0, result.cards.size());
        assertEquals(1, result.rejected.size());
        assertTrue(result.rejected.get(0).contains("dígitos fora da área do cartão"));
    }
}