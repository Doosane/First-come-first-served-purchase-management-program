package com.doosan.msa.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AESUtil {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "1234567890123456"; // Replace with secure retrieval method

    // Generate a valid AES key
    // 올바른 AES 키 생성
    public static SecretKeySpec generateKey(String inputKey) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(inputKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, 0, 16, "AES"); // "AES" 알고리즘을 명시적으로 지정
    }


    // Encrypt data
    public static String encrypt(String data) {
        try {
            SecretKeySpec keySpec = generateKey(SECRET_KEY);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    // Decrypt data
    public static String decrypt(String encryptedData) {
        try {
            SecretKeySpec keySpec = generateKey(SECRET_KEY);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
