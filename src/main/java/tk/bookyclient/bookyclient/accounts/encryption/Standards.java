package tk.bookyclient.bookyclient.accounts.encryption;

import net.minecraft.client.Minecraft;
import tk.bookyclient.bookyclient.accounts.utils.AccountConfig;
import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;
import tk.bookyclient.bookyclient.accounts.model.ExtendedAccountData;
import tk.bookyclient.bookyclient.utils.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

public final class Standards {

    public static File FOLDER = Constants.ACCOUNT_SWITCHER_DIR;
    public static final String config = "config.iasx", passwords = "passwords.iasp";

    public static String getPassword() {
        File passwordFile = new File(FOLDER, passwords);
        if (passwordFile.exists()) {
            String password;

            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(passwordFile));
                password = (String) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }

            return password;
        } else {
            String newPassword = EncryptionTools.generatePassword();
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(passwordFile));
                out.writeObject(newPassword);
                out.close();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }

            try {
                Path file = passwordFile.toPath();
                DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class);
                DosFileAttributeView view = Files.getFileAttributeView(file, DosFileAttributeView.class);

                if (!attr.isHidden()) view.setHidden(true);
            } catch (Exception exception) {
                throw new Error(exception);
            }
            return newPassword;
        }
    }

    public static void importAccounts() {
        processData(getConfigV3());
        processData(getConfigV2());
        processData(getConfigV1(), false);
    }

    private static boolean hasData(AccountData data) {
        for (AccountData accountData : AccountDatabase.getInstance().getAccounts())
            if (accountData.equalsBasic(data)) return true;
        return false;
    }

    private static void processData(AccountConfig oldData) {
        processData(oldData, true);
    }

    private static void processData(AccountConfig oldData, boolean decrypt) {
        if (oldData != null) {
            for (AccountData data : ((AccountDatabase) oldData.getKey("altaccounts")).getAccounts()) {
                AccountData data2 = convertData(data, decrypt);

                if (!hasData(data2)) AccountDatabase.getInstance().getAccounts().add(data2);
            }
        }
    }

    private static ExtendedAccountData convertData(AccountData oldData, boolean decrypt) {
        if (decrypt)
            if (oldData instanceof ExtendedAccountData)
                return new ExtendedAccountData(EncryptionTools.decodeOld(oldData.user), EncryptionTools.decodeOld(oldData.password), oldData.alias, ((ExtendedAccountData) oldData).useCount, ((ExtendedAccountData) oldData).lastUsed, ((ExtendedAccountData) oldData).premium);
            else
                return new ExtendedAccountData(EncryptionTools.decodeOld(oldData.user), EncryptionTools.decodeOld(oldData.password), oldData.alias);
        else if (oldData instanceof ExtendedAccountData)
            return new ExtendedAccountData(oldData.user, oldData.password, oldData.alias, ((ExtendedAccountData) oldData).useCount, ((ExtendedAccountData) oldData).lastUsed, ((ExtendedAccountData) oldData).premium);
        else
            return new ExtendedAccountData(oldData.user, oldData.password, oldData.alias);
    }

    private static AccountConfig getConfigV3() {
        File file = new File(FOLDER, ".ias");
        AccountConfig config = null;

        if (file.exists())
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                config = (AccountConfig) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException exception) {
                throw new Error(exception);
            }

        file.delete();
        return config;
    }

    private static AccountConfig getConfigV2() {
        File file = new File(Minecraft.getMinecraft().mcDataDir, ".ias");
        AccountConfig config = null;

        if (file.exists())
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                config = (AccountConfig) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException exception) {
                throw new Error(exception);
            }

        file.delete();
        return config;
    }

    private static AccountConfig getConfigV1() {
        File file = new File(Minecraft.getMinecraft().mcDataDir, "user.cfg");
        AccountConfig config = null;

        if (file.exists())
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                config = (AccountConfig) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException exception) {
                throw new Error(exception);
            }

        file.delete();
        return config;
    }
}
