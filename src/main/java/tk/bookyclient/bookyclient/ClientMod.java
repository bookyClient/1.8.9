package tk.bookyclient.bookyclient;
// Created by booky10 in bookyClient (19:14 29.12.20)

import tk.bookyclient.bookyclient.accounts.Accounts;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;
import tk.bookyclient.bookyclient.mixins.client.MinecraftAccessor;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.ClientResourcePack;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class ClientMod {

    public static void preStart(Minecraft minecraft) {
        Constants.LOGGER.info("Pre-Loading " + Constants.MOD_NAME + "...");

        ((MinecraftAccessor) minecraft).getDefaultResourcePacks().add(ClientResourcePack.getInstance());
        ClientSettings.loadSettings();
    }

    public static void start() {
        Accounts.load();
    }

    public static void postStart() {
        Constants.LOGGER.info("Post-Loading " + Constants.MOD_NAME + "...");

        SkinUtils.cacheSkins();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ClientSettings.saveSettings(false), Constants.MOD_NAME + " Config Saver Thread"));

        Constants.LOGGER.info("Starting auto config saver timer");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ClientSettings.saveSettings(true);
            }
        }, 1000 * 5, 1000 * 10);
    }
}
