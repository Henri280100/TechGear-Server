package com.v01.techgear_server.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeyUtils {
    @Autowired
    Environment environment;

    @Value("${access-token.private}")
    private String accessTokenPrivateKeyPath;

    @Value("${access-token.public}")
    private String accessTokenPublicKeyPath;

    @Value("${refresh-token.private}")
    private String refreshTokenPrivateKeyPath;

    @Value("${refresh-token.public}")
    private String refreshTokenPublicKeyPath;

    private KeyPair _accessTokenKeyPair;
    private KeyPair _refreshTokenKeyPair;

    /**
     * Get the access token key pair. The key pair is fetched from the file
     * system and stored in memory. If the key pair is not found, or if there is
     * an error while reading the files, an exception is thrown.
     * 
     * @return The access token key pair.
     */
    private KeyPair getAccessTokenKeyPair() {
        if (Objects.isNull(_accessTokenKeyPair)) {
            _accessTokenKeyPair = getKeyPair(accessTokenPublicKeyPath, accessTokenPrivateKeyPath);
        }
        return _accessTokenKeyPair;
    }

    /**
     * Get the refresh token key pair. The key pair is fetched from the file
     * system and stored in memory. If the key pair is not found, or if there is
     * an error while reading the files, an exception is thrown.
     * 
     * @return The refresh token key pair.
     */
    private KeyPair getRefreshTokenKeyPair() {
        if (Objects.isNull(_refreshTokenKeyPair)) {
            _refreshTokenKeyPair = getKeyPair(refreshTokenPublicKeyPath, refreshTokenPrivateKeyPath);
        }
        return _refreshTokenKeyPair;
    }

    /**
     * Get a key pair from the file system. If the key pair doesn't exist, generate
     * a new one
     * and save it to the file system.
     * 
     * @param publicKeyPath  The path to the public key
     * @param privateKeyPath The path to the private key
     * @return The key pair
     */
    private KeyPair getKeyPair(String publicKeyName, String privateKeyName) {
        KeyPair keyPair;

        // Construct the paths to the public and private keys inside src/main/resources
        String keysDirectory = System.getProperty("user.dir") + "/src/main/resources/access-refresh-token-keys";

        // Construct the full paths for the keys
        String publicKeyPath = keysDirectory + File.separator + publicKeyName;
        String privateKeyPath = keysDirectory + File.separator + privateKeyName;

        File publicKeyFile = new File(publicKeyPath);
        File privateKeyFile = new File(privateKeyPath);

        // Check if the public and private key files already exist
        if (publicKeyFile.exists() && privateKeyFile.exists()) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

                byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

                keyPair = new KeyPair(publicKey, privateKey);
                return keyPair;
            } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (Arrays.stream(environment.getActiveProfiles()).anyMatch(s -> s.equals("prod"))) {
                throw new RuntimeException("Public and private keys don't exist");
            }
        }

        // Ensure the directory exists
        File directory = new File(keysDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // Generate new public and private keys and save them
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();

            // Save the public key
            try (FileOutputStream fos = new FileOutputStream(publicKeyPath)) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
                fos.write(keySpec.getEncoded());
            }

            // Save the private key
            try (FileOutputStream fos = new FileOutputStream(privateKeyPath)) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
                fos.write(keySpec.getEncoded());
            }

        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

        return keyPair;
    }

    public RSAPublicKey getAccessTokenPublicKey() {
        return (RSAPublicKey) getAccessTokenKeyPair().getPublic();
    };

    public RSAPrivateKey getAccessTokenPrivateKey() {
        return (RSAPrivateKey) getAccessTokenKeyPair().getPrivate();
    };

    public RSAPublicKey getRefreshTokenPublicKey() {
        return (RSAPublicKey) getRefreshTokenKeyPair().getPublic();
    };

    public RSAPrivateKey getRefreshTokenPrivateKey() {
        return (RSAPrivateKey) getRefreshTokenKeyPair().getPrivate();
    };
}
