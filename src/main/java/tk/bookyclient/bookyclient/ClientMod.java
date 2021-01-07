package tk.bookyclient.bookyclient;
// Created by booky10 in bookyClient (19:14 29.12.20)

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tk.bookyclient.bookyclient.accounts.encryption.Standards;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;
import tk.bookyclient.bookyclient.accounts.utils.AccountConfig;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class ClientMod {

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Constants.LOGGER.info("Pre-Loading " + Constants.MOD_NAME + "...");
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        Constants.LOGGER.info("Loading " + Constants.MOD_NAME + "...");

        AccountConfig.readFromFile();
        Standards.importAccounts();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
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
