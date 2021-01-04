package tk.bookyclient.bookyclient.settings;
// Created by booky10 in MCP 1.8.9 (19:37 25.08.20)

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tk.bookyclient.bookyclient.utils.Constants;

import java.io.*;
import java.util.ArrayList;

public class ClientSettings implements Serializable {

    // Main toggles
    public boolean toggleSprint = false;
    public boolean clearChatBackground = false;
    public boolean clearChatBar = false;
    public boolean clearScoreboardBackground = false;
    public boolean scoreboardRedScores = false;
    public boolean FOVModifier = false;
    public boolean asyncScreenshots = true;
    public boolean crashScreen = true;
    public boolean mouseDelayFix = true;
    public boolean numeralPing = false;
    public boolean invisibleTablist = false;
    public boolean nameTagBackground = true;
    public boolean blurGuiBackground = false;
    public boolean itemPhysics = false;
    public boolean addressInRPC = true;
    public boolean betterHead = false;
    public boolean betterSwing = false;
    public boolean smoothScrolling = false;
    public boolean scrollableTooltips = false;
    public boolean renderOwnName = false;
    public boolean repeatingSoundFix = true;
    public boolean windowedFullscreen = true;

    // Temporary values
    public static boolean zoom = false;
    public static boolean perspective = false;

    // MotionBlur
    public boolean motionBlur = false;
    public float motionBlurMultiplier = 1.0F;

    // Keystrokes - booleans
    public boolean keystrokes = false;
    public boolean keystrokesChroma = false;
    public boolean keystrokesMouseButtons = false;
    public boolean keystrokesCPS = false;
    public boolean keystrokesSpacebar = false;
    public boolean keystrokesPing = false;
    public boolean keystrokesFPS = false;
    public boolean keystrokesKeyBackground = true;
    public boolean keystrokesSneak = false;
    public boolean keystrokesWASD = true;
    public boolean keystrokesReach = false;
    public int keystrokesX = 0;
    public int keystrokesY = 0;
    public int keystrokesRed = 255;
    public int keystrokesGreen = 255;
    public int keystrokesBlue = 255;
    public double keystrokesScale = 1D;
    public ArrayList<?> keystrokesCustomKeys = new ArrayList<>();

    // Wings
    public boolean wings = false;
    public boolean wingsChroma = false;
    public int wingsRed = 255;
    public int wingsGreen = 255;
    public int wingsBlue = 255;
    public int wingsOpacity = 100;

    // Creeper Armor
    public boolean creeperArmor = false;
    public boolean creeperArmorChroma = false;
    public int creeperArmorRed = 255;
    public int creeperArmorGreen = 255;
    public int creeperArmorBlue = 255;
    public int creeperArmorOpacity = 100;

    // Wither Armor
    public boolean witherArmor = false;
    public boolean witherArmorChroma = false;
    public int witherArmorRed = 255;
    public int witherArmorGreen = 255;
    public int witherArmorBlue = 255;
    public int witherArmorOpacity = 100;

    // Blaze Rods
    public boolean blazeRods = false;
    public boolean blazeRodsChroma = false;
    public int blazeRodsRed = 255;
    public int blazeRodsGreen = 255;
    public int blazeRodsBlue = 255;
    public int blazeRodsOpacity = 100;

    // Old Animations
    public boolean oldAnimations = false;
    public boolean oldAnimationsRod = false;
    public boolean oldAnimationsPunching = false;
    public boolean oldAnimationsBlockHitting = false;

    // Other things
    private static ClientSettings instance;
    private static boolean saving = false;
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final File FILE = new File(Constants.CLIENT_DIR, "config.json");

    protected ClientSettings() {
    }

    public static ClientSettings getInstance() {
        loadSettings();
        return instance;
    }

    public static void loadSettings() {
        if (instance != null) return;

        if (FILE.exists())
            try (FileReader reader = new FileReader(FILE)) {
                instance = GSON.fromJson(reader, ClientSettings.class);
            } catch (IOException exception) {
                throw new Error(exception);
            }
        else {
            instance = new ClientSettings();
            saveSettings(false);
        }
    }

    public static void saveSettings(boolean async) {
        if (saving) return;
        saving = true;

        Runnable runnable = () -> {
            try (FileWriter writer = new FileWriter(FILE)) {
                GSON.toJson(instance, writer);
            } catch (IOException exception) {
                throw new Error(exception);
            }
        };

        if (async) new Thread(runnable, "bookyClient Config Saver Thread");
        else runnable.run();
    }
}