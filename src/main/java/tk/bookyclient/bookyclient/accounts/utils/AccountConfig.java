package tk.bookyclient.bookyclient.accounts.utils;

import net.minecraft.client.Minecraft;
import tk.bookyclient.bookyclient.accounts.encryption.Standards;
import tk.bookyclient.bookyclient.utils.Constants;
import tk.bookyclient.bookyclient.utils.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;

public class AccountConfig implements Serializable {

    private static AccountConfig instance = null;
    private static final String configFileName = Standards.config;
    private final ArrayList<Pair<String, Object>> user;

    public static AccountConfig getInstance() {
        return instance;
    }

    private AccountConfig() {
        user = new ArrayList<>();
        instance = this;
    }

    public void setKey(Pair<String, Object> key) {
        if (getKey(key.getKey()) != null) removeKey(key.getKey());

        user.add(key);
        save();
    }

    public void setKey(String key, Object value) {
        setKey(new Pair<>(key, value));
    }

    public Object getKey(String key) {
        for (Pair<String, Object> user : user)
            if (user.getKey().equals(key))
                return user.getValue();
        return null;
    }

    private void removeKey(String key) {
        user.removeIf(user -> user.getKey().equals(key));
    }

    public static void save() {
        saveToFile();
    }

    public static void load() {
        loadFromOld();
        readFromFile();
    }

    private static void readFromFile() {
        File file = new File(Standards.FOLDER, configFileName);

        if (file.exists()) {
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                instance = (AccountConfig) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
                instance = new AccountConfig();
                file.delete();
            }
        }

        if (instance == null) instance = new AccountConfig();
    }

    private static void saveToFile() {
        try {
            Path file = new File(Standards.FOLDER, configFileName).toPath();
            DosFileAttributes attributes = Files.readAttributes(file, DosFileAttributes.class);
            DosFileAttributeView view = Files.getFileAttributeView(file, DosFileAttributeView.class);
            if (attributes.isHidden()) view.setHidden(false);
        } catch (NoSuchFileException ignored) {
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(Standards.FOLDER, configFileName)));
            out.writeObject(instance);
            out.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            Path file = new File(Standards.FOLDER, configFileName).toPath();
            DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class);
            DosFileAttributeView view = Files.getFileAttributeView(file, DosFileAttributeView.class);

            if (!attr.isHidden()) view.setHidden(true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void loadFromOld() {
        File file = new File(Minecraft.getMinecraft().mcDataDir, "user.cfg");
        if (!file.exists()) return;

        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            instance = (AccountConfig) stream.readObject();
            stream.close();
            file.delete();

            Constants.LOGGER.info("Loaded data from old file");
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            file.delete();
        }
    }
}
