package com.smartnotes.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final int ITERATIONS = 10000;

    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            for (int i = 0; i < ITERATIONS - 1; i++) {
                md.reset();
                md.update(hashedPassword);
                hashedPassword = md.digest();
            }

            // Combine salt and hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String hash) {
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(hash);

            // Extract salt
            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, 16);

            // Hash the password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            for (int i = 0; i < ITERATIONS - 1; i++) {
                md.reset();
                md.update(hashedPassword);
                hashedPassword = md.digest();
            }

            // Compare hashes
            byte[] storedHash = new byte[saltAndHash.length - 16];
            System.arraycopy(saltAndHash, 16, storedHash, 0, storedHash.length);

            return MessageDigest.isEqual(hashedPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
