package br.com.hyperativa.util;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class HmacUtil {

    @Value("${key.lookup}")
    private String keyLookup;

    public String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(keyLookup.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] digest = mac.doFinal(data.getBytes());
            return Hex.encodeHexString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
