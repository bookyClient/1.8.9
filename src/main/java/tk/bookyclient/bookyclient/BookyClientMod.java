package tk.bookyclient.bookyclient;
// Created by booky10 in bookyClient (19:14 29.12.20)

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tk.bookyclient.bookyclient.accounts.encryption.Standards;
import tk.bookyclient.bookyclient.accounts.events.RenderTextEvents;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;
import tk.bookyclient.bookyclient.accounts.utils.AccountConfig;
import tk.bookyclient.bookyclient.utils.Constants;

@Mod(modid = Constants.MOD_ID, version = Constants.VERSION, clientSideOnly = true, name = Constants.MOD_NAME)
public class BookyClientMod {

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Constants.createDirs();

        if (event.getModMetadata().version.equals("${version}"))
            System.out.println("Detected Development Environment!");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        AccountConfig.load();
        Standards.importAccounts();

        MinecraftForge.EVENT_BUS.register(new RenderTextEvents());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        SkinUtils.cacheSkins();
    }
}
