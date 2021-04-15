package tk.bookyclient.bookyclient.utils;
// Created by booky10 in bookyClient (19:12 29.12.20)

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

public final class Constants {

    public static final boolean DEBUG = Constants.class.getPackage().getImplementationTitle() == null;
    public static final Utilities UTILITIES = new Utilities();

    public static final String AUTHOR = DEBUG ? "DEV" : Constants.class.getPackage().getImplementationVendor();
    public static final String VERSION = DEBUG ? "DEV" : Constants.class.getPackage().getImplementationVersion();
    public static final String MOD_NAME = DEBUG ? "bookyClient" : Constants.class.getPackage().getImplementationTitle();
    public static final String MOD_ID = MOD_NAME.toLowerCase();

    public static final MetadataCollection METADATA_COLLECTION = MetadataCollection.from(Constants.class.getResourceAsStream("mcmod.info"), Constants.MOD_ID);
    public static final ModMetadata METADATA = METADATA_COLLECTION.getMetadataForId(MOD_ID, UTILITIES.createMap(new Pair<>("name", MOD_NAME), new Pair<>("version", VERSION)));
    public static final DummyModContainer MOD_CONTAINER = new DummyModContainer(UTILITIES.fillMetadata(Constants.METADATA));

    public static final File MINECRAFT_DIR = (Launch.minecraftHome == null ? new File(".") : Launch.minecraftHome);
    public static final File SCREENSHOT_DIR = new File(MINECRAFT_DIR, "screenshots");
    public static final File CRASH_REPORT_DIR = new File(MINECRAFT_DIR, "crash-reports");
    public static final File CLIENT_DIR = new File(MINECRAFT_DIR, MOD_NAME);
    public static final File CACHE_DIR = new File(CLIENT_DIR, "cache");
    public static final File ACCOUNT_SWITCHER_DIR = new File(CLIENT_DIR, "accounts");

    public static final int ACCOUNTS_BUTTON_ID = new Random().nextInt(Integer.MAX_VALUE);
    public static final int SETTINGS_BUTTON_ID = new Random().nextInt(Integer.MAX_VALUE);

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final String USER_AGENT = String.format("Minecraft/Forge%s/%s%s/%s", ForgeVersion.getVersion(), MOD_NAME, VERSION, AUTHOR);
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void createDirs() {
        for (Field field : Constants.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object object = field.get(null);
                if (!(object instanceof File)) continue;

                ((File) object).mkdirs();
            } catch (Throwable ignored) {
            }
        }
    }
}