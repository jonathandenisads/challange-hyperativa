package br.com.hyperativa.util;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class HmacUtilTest {

    @Test
    void testHmacSha256() {
        HmacUtil hmac = new HmacUtil();
        // Define a chave manualmente (substituindo o @Value)
        ReflectionTestUtils.setField(hmac, "keyLookup", "my-secret-key");

        String data = "hello world";
        String result = hmac.hmacSha256(data);

        assertNotNull(result);
        assertEquals(64, result.length()); // SHA-256 hex tem 64 caracteres
        // Opcional: verificar um valor esperado (gerado previamente)
        assertEquals("aca623f67205e1004f562ec958df16fa4456c3ff859d3d2913f9ea764e58fac2",
                hmac.hmacSha256("test")); // exemplo
    }
    @Test
    void testHmacWithEmptyKey() {
        HmacUtil hmac = new HmacUtil();
        ReflectionTestUtils.setField(hmac, "keyLookup", "");

        assertThrows(RuntimeException.class, () -> hmac.hmacSha256("hello"));
    }
}
