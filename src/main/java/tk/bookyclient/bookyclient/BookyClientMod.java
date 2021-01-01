package tk.bookyclient.bookyclient;
// Created by booky10 in bookyClient (19:14 29.12.20)

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tk.bookyclient.bookyclient.accounts.encryption.Standards;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;
import tk.bookyclient.bookyclient.accounts.utils.AccountConfig;
import tk.bookyclient.bookyclient.utils.Constants;

import java.io.File;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class BookyClientMod {

    @Mod.Instance(Constants.MOD_ID)
    public static BookyClientMod instance;
    public static File mcDir;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        Constants.LOGGER.info("Pre-Loading " + Constants.MOD_NAME + "...");

        Constants.createDirs();
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
    }
}
