package br.com.hyperativa.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AesUtil {

    @Value("${key.enc}")
    private String keyEnc;

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private SecretKeySpec getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(keyEnc);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plain) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), new GCMParameterSpec(TAG_LENGTH, iv));

            byte[] cipherText = cipher.doFinal(plain.getBytes());
            byte[] out = new byte[iv.length + cipherText.length];

            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(cipherText, 0, out, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String cipherTextB64) {
        try {
            byte[] all = Base64.getDecoder().decode(cipherTextB64);

            byte[] iv = Arrays.copyOfRange(all, 0, IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(all, IV_LENGTH, all.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new GCMParameterSpec(TAG_LENGTH, iv));

            return new String(cipher.doFinal(cipherText));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
