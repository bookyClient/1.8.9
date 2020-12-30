package tk.bookyclient.bookyclient.utils;
// Created by booky10 in bookyClient (19:12 29.12.20)

import net.minecraft.client.Minecraft;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Random;

public final class Constants {

    public static final String MOD_ID = "bookyclient";
    public static final String MOD_NAME = "bookyClient";
    public static final String VERSION = "1.0-SNAPSHOT";

    public static final File CLIENT_DIR = new File(Minecraft.getMinecraft().mcDataDir, "bookyClient");
    public static final File CACHE_DIR = new File(CLIENT_DIR, "cache");
    public static final File ACCOUNT_SWITCHER_DIR = new File(CLIENT_DIR, "accountSwitcher");

    public static final Integer ACCOUNTS_BUTTON_ID = new Random().nextInt(Integer.MAX_VALUE);

    public static void createDirs() {
        for (Field field : Constants.class.getDeclaredFields())
            try {
                field.setAccessible(true);
                Object object = field.get(null);

                if (object instanceof File) ((File) object).mkdirs();
            } catch (Throwable ignored) {
            }
    }
}