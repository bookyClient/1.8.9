package tk.bookyclient.bookyclient.utils;
// Created by booky10 in bookyClient (19:12 29.12.20)

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Random;

public final class Constants {

    public static final boolean DEBUG = Constants.class.getPackage().getImplementationTitle() == null;

    public static final String VERSION = DEBUG ? "DEV" : Constants.class.getPackage().getImplementationVersion();
    public static final String MOD_NAME = DEBUG ? "bookyClient" : Constants.class.getPackage().getImplementationTitle();
    public static final String MOD_ID = MOD_NAME.toLowerCase();

    public static final File MINECRAFT_DIR = (Launch.minecraftHome == null ? new File(".") : Launch.minecraftHome);
    public static final File CLIENT_DIR = new File(MINECRAFT_DIR, MOD_NAME);
    public static final File CACHE_DIR = new File(CLIENT_DIR, "cache");
    public static final File ACCOUNT_SWITCHER_DIR = new File(CLIENT_DIR, "accounts");

    public static final Integer ACCOUNTS_BUTTON_ID = new Random().nextInt(Integer.MAX_VALUE);
    public static final Integer SETTINGS_BUTTON_ID = new Random().nextInt(Integer.MAX_VALUE);

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void createDirs() {
        for (Field field : Constants.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object object = field.get(null);

                if (object instanceof File) ((File) object).mkdirs();
            } catch (Throwable ignored) {
            }
        }
    }
}