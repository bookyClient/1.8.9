package tk.bookyclient.bookyclient.accounts;

import tk.bookyclient.bookyclient.utils.Constants;
import tk.bookyclient.bookyclient.utils.Utilities;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public final class Accounts {

    private static final MessageDigest HASHER = Utilities.getSHA512Hasher();
    private static final KeyGenerator GENERATOR = Utilities.getAESGenerator();

    public static String encode(String text) {
        try {
            byte[] data = text.getBytes(StandardCharsets.UTF_8);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getCipherKey());

            return new String(Base64.getEncoder().encode(cipher.doFinal(data)));
        } catch (BadPaddingException exception) {
            throw new Error("The password doesn't match", exception);
        } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new Error(exception);
        }
    }

    public static String decode(String text) {
        try {
            byte[] data = Base64.getDecoder().decode(text);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getCipherKey());

            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (BadPaddingException exception) {
            throw new Error("The password doesn't match", exception);
        } catch (IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new Error(exception);
        }
    }

    public static String getCipherPassword() {
        File passwordFile = new File(Constants.ACCOUNT_SWITCHER_DIR, "password.dat");

        if (passwordFile.exists()) {
            String password;

            try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(passwordFile))) {
                password = (String) stream.readObject();
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }

            return password;
        } else {
            String password = Accounts.generateCipherPassword();

            try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(passwordFile))) {
                stream.writeObject(password);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            return password;
        }
    }

    private static String generateCipherPassword() {
        GENERATOR.init(256);
        return new String(Base64.getEncoder().encode(GENERATOR.generateKey().getEncoded()));
    }

    private static SecretKeySpec getCipherKey() {
        return new SecretKeySpec(Arrays.copyOf(HASHER.digest(getCipherPassword().getBytes(StandardCharsets.UTF_8)), 16), "AES");
    }

    public static void load() {
        File file = new File(Constants.ACCOUNT_SWITCHER_DIR, "accounts.dat");

        if (file.exists()) {
            try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
                ((AccountDatabase) stream.readObject()).setInstance();
            } catch (IOException | ClassNotFoundException exception) {
                throw new Error(exception);
            }
        } else {
            new AccountDatabase().setInstance();
        }
    }

    public static void save() {
        File file = new File(Constants.ACCOUNT_SWITCHER_DIR, "accounts.dat");

        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
            stream.writeObject(AccountDatabase.getInstance());
        } catch (IOException exception) {
            throw new Error(exception);
        }
    }
}
