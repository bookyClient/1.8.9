package tk.bookyclient.bookyclient.accounts.encryption;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public final class EncryptionTools {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final MessageDigest sha512 = getSHA512Hasher();
    private static final KeyGenerator keyGen = getAESGenerator();

    public static String decodeOld(String text) {
        return new String(decoder.decode(text), StandardCharsets.UTF_8);
    }

    public static String encode(String text) {
        try {
            byte[] data = text.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            return new String(encoder.encode(cipher.doFinal(data)));
        } catch (BadPaddingException exception) {
            throw new Error("The password doesn't match", exception);
        } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new Error(exception);
        }
    }

    public static String decode(String text) {
        try {
            byte[] data = decoder.decode(text);
            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (BadPaddingException exception) {
            throw new Error("The password doesn't match", exception);
        } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new Error(exception);
        }
    }

    public static String generatePassword() {
        keyGen.init(256);
        return new String(encoder.encode(keyGen.generateKey().getEncoded()));
    }

    private static MessageDigest getSHA512Hasher() {
        try {
            return MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException exception) {
            throw new Error(exception);
        }
    }

    private static KeyGenerator getAESGenerator() {
        try {
            return KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException exception) {
            throw new Error(exception);
        }
    }

    private static SecretKeySpec getSecretKey() {
        String secretSalt = "lIlIlIlIlIIllII";
        String password = secretSalt + Standards.getPassword() + secretSalt;
        byte[] key = Arrays.copyOf(sha512.digest(password.getBytes(StandardCharsets.UTF_8)), 16);

        return new SecretKeySpec(key, "AES");
    }
}
