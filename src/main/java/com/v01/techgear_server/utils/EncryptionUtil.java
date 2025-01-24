package com.v01.techgear_server.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static SecretKey secretKey;

    private EncryptionUtil(@Value("${techgear-secretkey:}") String key) {
        secretKey = (key == null || key.trim().isEmpty()) ? generateKey() : createSecretKeyFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey createSecretKeyFromBytes(byte[] keyBytes) {
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length: " + keyBytes.length);
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    private static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256, new SecureRandom()); // 256-bit key generation
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to generate AES key", e);
        }
    }

    public static String encrypt(String input) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            String base64Encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            // Return only the first 5 characters of the encrypted string
            return base64Encrypted.substring(0, 5);    
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encrypt input", e);
        }
        
    }

    public static String decrypt(String encryptedInput) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedInput));
            return new String(decryptedBytes, StandardCharsets.UTF_8);    
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decrypt input", e);
        }
        
        
    }
}
