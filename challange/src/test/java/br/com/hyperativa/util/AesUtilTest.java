package br.com.hyperativa.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

public class AesUtilTest {

    @Test
    void testEncryptDecrypt() {
        AesUtil aes = new AesUtil();
        ReflectionTestUtils.setField(aes, "keyEnc", Base64.getEncoder().encodeToString("1234567890123456".getBytes()));

        String original = "hello world";
        String encrypted = aes.encrypt(original);
        String decrypted = aes.decrypt(encrypted);

        Assertions.assertNotEquals(original, encrypted); // verifica se foi criptografado
        Assertions.assertEquals(original, decrypted);   // verifica se descriptografou corretamente
    }
    @Test
    void testInvalidKey() {
        AesUtil aes = new AesUtil();
        ReflectionTestUtils.setField(aes, "keyEnc", "invalid-base64");

        Assertions.assertThrows(RuntimeException.class, () -> aes.encrypt("test"));
    }

    @Test
    void testInvalidKeyDecrypt() {
        AesUtil aes = new AesUtil();
        ReflectionTestUtils.setField(aes, "keyEnc", "invalid-base64");

        Assertions.assertThrows(RuntimeException.class, () -> aes.decrypt("test"));
    }
}
