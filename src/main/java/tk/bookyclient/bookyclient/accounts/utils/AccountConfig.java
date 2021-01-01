package tk.bookyclient.bookyclient.accounts.utils;

import tk.bookyclient.bookyclient.accounts.encryption.Standards;
import tk.bookyclient.bookyclient.utils.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class AccountConfig implements Serializable {

    private static AccountConfig instance;
    private final List<Pair<String, Object>> accounts=new ArrayList<>();

    public static AccountConfig getInstance() {
        return instance;
    }

    private AccountConfig() {
        instance = this;
    }

    public void setKey(Pair<String, Object> key) {
        if (getKey(key.getKey()) != null) removeKey(key.getKey());

        accounts.add(key);
        saveToFile();
    }

    public void setKey(String key, Object value) {
        setKey(new Pair<>(key, value));
    }

    public Object getKey(String key) {
        for (Pair<String, Object> account : accounts) {
            if (!account.getKey().equals(key)) continue;
            return account.getValue();
        }
        return null;
    }

    private void removeKey(String key) {
        accounts.removeIf(account -> account.getKey().equals(key));
    }

    public static void saveToFile() {
        File file = new File(Standards.FOLDER, Standards.config);

        try {
            if (file.exists()) {
                Path path = file.toPath();
                DosFileAttributes attributes = Files.readAttributes(path, DosFileAttributes.class);
                DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class);

                if (attributes.isHidden()) view.setHidden(false);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        try (FileOutputStream output = new FileOutputStream(file)) {
            try (ObjectOutputStream stream = new ObjectOutputStream(output)) {
                stream.writeObject(instance);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            if (file.exists()) {
                Path path = file.toPath();
                DosFileAttributes attr = Files.readAttributes(path, DosFileAttributes.class);
                DosFileAttributeView view = Files.getFileAttributeView(path, DosFileAttributeView.class);

                if (!attr.isHidden()) view.setHidden(true);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void readFromFile() {
        File file = new File(Standards.FOLDER, Standards.config);

        if (file.exists()) {
            try (FileInputStream input = new FileInputStream(file)) {
                try (ObjectInputStream stream = new ObjectInputStream(input)) {
                    instance = (AccountConfig) stream.readObject();
                }
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
                instance = new AccountConfig();
                file.delete();
            }
        }

        if (instance == null) instance = new AccountConfig();
    }
}
