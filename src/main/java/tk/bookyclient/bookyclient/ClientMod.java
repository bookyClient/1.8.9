package tk.bookyclient.bookyclient;
// Created by booky10 in bookyClient (19:14 29.12.20)

import tk.bookyclient.bookyclient.accounts.Accounts;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;
import tk.bookyclient.bookyclient.mixins.main.MinecraftAccessor;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.ClientResourcePack;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class ClientMod {

    public static void preStart(MinecraftAccessor minecraft) {
        minecraft.getDefaultResourcePacks().add(ClientResourcePack.getInstance());
        ClientSettings.loadSettings();
    }

    public static void start() {
        Accounts.load();
        Constants.UTILITIES.registerKeys();
    }

    public static void postStart() {
        SkinUtils.cacheSkins();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ClientSettings.saveSettings(false), Constants.MOD_NAME + " Config Saver Thread"));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ClientSettings.saveSettings(true);
            }
        }, 1000 * 5, 1000 * 10);
    }
}
