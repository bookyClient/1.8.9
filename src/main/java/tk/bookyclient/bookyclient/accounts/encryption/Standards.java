package tk.bookyclient.bookyclient.accounts.encryption;

import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.accounts.model.ExtendedAccountData;
import tk.bookyclient.bookyclient.accounts.utils.AccountConfig;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;
import tk.bookyclient.bookyclient.utils.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

public final class Standards {

    public static final File FOLDER = Constants.ACCOUNT_SWITCHER_DIR;
    public static final String config = "config.iasx", passwords = "passwords.iasp";

    public static String getPassword() {
        File passwordFile = new File(FOLDER, passwords);

        if (passwordFile.exists()) {
            String password;

            try (FileInputStream input = new FileInputStream(passwordFile)) {
                try (ObjectInputStream stream = new ObjectInputStream(input)) {
                    password = (String) stream.readObject();
                }
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
            return password;
        } else {
            String newPassword = EncryptionTools.generatePassword();

            try (FileOutputStream output = new FileOutputStream(passwordFile)) {
                try (ObjectOutputStream stream = new ObjectOutputStream(output)) {
                    stream.writeObject(newPassword);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            try {
                Path file = passwordFile.toPath();
                DosFileAttributes attributes = Files.readAttributes(file, DosFileAttributes.class);
                DosFileAttributeView attributeView = Files.getFileAttributeView(file, DosFileAttributeView.class);

                if (!attributes.isHidden()) attributeView.setHidden(true);
            } catch (Throwable ignored) {
            }
            return newPassword;
        }
    }

    private static boolean hasData(AccountData data) {
        for (AccountData account : AccountDatabase.getInstance().getAccounts()) {
            if (!account.equalsBasic(data)) continue;
            return true;
        }
        return false;
    }

    public static void importAccounts() {
        AccountConfig config = getConfig();
        if (config == null) return;

        for (AccountData account : ((AccountDatabase) config.getKey("accounts")).getAccounts()) {
            AccountData converted = convertData(account);

            if (hasData(converted)) continue;
            AccountDatabase.getInstance().getAccounts().add(converted);
        }
    }

    private static ExtendedAccountData convertData(AccountData account) {
        if (account instanceof ExtendedAccountData)
            return new ExtendedAccountData(EncryptionTools.decodeOld(account.user), EncryptionTools.decodeOld(account.password), account.alias, ((ExtendedAccountData) account).useCount, ((ExtendedAccountData) account).lastUsed, ((ExtendedAccountData) account).premium);
        else
            return new ExtendedAccountData(EncryptionTools.decodeOld(account.user), EncryptionTools.decodeOld(account.password), account.alias);
    }

    public static AccountConfig getConfig() {
        File configFile = new File(FOLDER, ".ias");

        if (!configFile.exists()) return null;

        AccountConfig config;
        try (FileInputStream input = new FileInputStream(configFile)) {
            try (ObjectInputStream stream = new ObjectInputStream(input)) {
                config = (AccountConfig) stream.readObject();
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }

        configFile.delete();
        return config;
    }
}
