package com.abach42.superhero.shared.convertion;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionHelper {
    @Value("${com.abach42.superhero.security.bothSiteEncryption.key}")
    private String key;
    @Value("${com.abach42.superhero.security.bothSiteEncryption.initVector}")
    private String initVector;

    public String encrypt(String value) {
        if (value == null) {
            return null;
        }

        if (value.isEmpty()) {
            return "";
        }

        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new EncryptionException("Error occurred while encrypting text", e);
        }
    }

    public String decrypt(String encrypted) {
        if (encrypted == null) {
            return null;
        }

        if (encrypted.isEmpty()) {
            return "";
        }

        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception e) {
            throw new EncryptionException("Error occurred while decrypting text", e);
        }
    }

    private Cipher getCipher(int encryptMode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec initializationVector = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        String algorithm = "AES/CBC/PKCS5PADDING";

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(encryptMode, secretKeySpec, initializationVector);

        return cipher;
    }

    private static class EncryptionException extends RuntimeException {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

