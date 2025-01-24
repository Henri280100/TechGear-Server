package com.v01.techgear_server.security;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeyUtils {
    private final Environment environment;

    @Value("${access-token.private}")
    private String accessTokenPrivateKeyPath;

    @Value("${access-token.public}")
    private String accessTokenPublicKeyPath;

    @Value("${refresh-token.private}")
    private String refreshTokenPrivateKeyPath;

    @Value("${refresh-token.public}")
    private String refreshTokenPublicKeyPath;

    private KeyPair accessTokenKeyPair;
    private KeyPair refreshTokenKeyPair;

    private final String keyDir = System.getProperty("user.dir") + "/src/main/resources/access-refresh-token-keys";

    @PostConstruct
    public void init() {
        accessTokenKeyPair = getAccessTokenKeyPair();
        refreshTokenKeyPair = getRefreshTokenKeyPair();
    }

    private KeyPair getAccessTokenKeyPair() {
        if (accessTokenKeyPair == null) {
            accessTokenKeyPair = loadOrGenerateKeyPair(
                keyDir + "/accessToken-public.pem",
                keyDir + "/accessToken-private.pem"
            );
        }
        return accessTokenKeyPair;
    }

    private KeyPair getRefreshTokenKeyPair() {
        if (refreshTokenKeyPair == null) {
            refreshTokenKeyPair = loadOrGenerateKeyPair(
                keyDir + "/refreshToken-public.pem",
                keyDir + "/refreshToken-private.pem"
            );
        }
        return refreshTokenKeyPair;
    }

    private KeyPair loadOrGenerateKeyPair(String publicKeyPath, String privateKeyPath) {
        File directory = new File(keyDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File publicKeyFile = new File(publicKeyPath);
        File privateKeyFile = new File(privateKeyPath);

        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            try {
                return loadKeyPair(publicKeyPath, privateKeyPath);
            } catch (Exception e) {
                log.error("Error loading key pair: {}", e.getMessage());
                throw new RuntimeException("Error loading key pair", e);
            }
        } else {
            if (isProductionProfile()) {
                throw new RuntimeException("Public and private keys must exist in production");
            }
            return generateAndSaveKeyPair(publicKeyPath, privateKeyPath);
        }
    }

    private KeyPair loadKeyPair(String publicKeyPath, String privateKeyPath) throws Exception {
        byte[] publicKeyContent = Files.readAllBytes(Path.of(publicKeyPath));
        byte[] privateKeyContent = Files.readAllBytes(Path.of(privateKeyPath));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Remove PEM headers and convert to binary
        byte[] publicKeyBinary = parsePEMKey(publicKeyContent);
        byte[] privateKeyBinary = parsePEMKey(privateKeyContent);

        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBinary));
        PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBinary));

        return new KeyPair(publicKey, privateKey);
    }

    private byte[] parsePEMKey(byte[] pemContent) {
        String key = new String(pemContent, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(key);
    }

    private KeyPair generateAndSaveKeyPair(String publicKeyPath, String privateKeyPath) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Save public key
            String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()) +
                    "\n-----END PUBLIC KEY-----";
            Files.write(Path.of(publicKeyPath), publicKeyPEM.getBytes());

            // Save private key
            String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()) +
                    "\n-----END PRIVATE KEY-----";
            Files.write(Path.of(privateKeyPath), privateKeyPEM.getBytes());

            return keyPair;
        } catch (Exception e) {
            throw new RuntimeException("Error generating key pair", e);
        }
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles()).anyMatch(s -> s.equals("prod"));
    }

    public RSAPublicKey getAccessTokenPublicKey() {
        return (RSAPublicKey) getAccessTokenKeyPair().getPublic();
    }

    public RSAPrivateKey getAccessTokenPrivateKey() {
        return (RSAPrivateKey) getAccessTokenKeyPair().getPrivate();
    }

    public RSAPublicKey getRefreshTokenPublicKey() {
        return (RSAPublicKey) getRefreshTokenKeyPair().getPublic();
    }

    public RSAPrivateKey getRefreshTokenPrivateKey() {
        return (RSAPrivateKey) getRefreshTokenKeyPair().getPrivate();
    }
}
