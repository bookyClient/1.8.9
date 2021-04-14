package tk.bookyclient.bookyclient.core;
// Created by booky10 in bookyClient (21:40 29.12.20)

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.Map;

public class ClientFMLSetupHook implements IFMLCallHook {

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public Void call() {
        Constants.LOGGER.info("Loading Constants...");
        Constants.createDirs();

        Constants.LOGGER.info("Injecting Mixins...");

        MixinBootstrap.init();
        Mixins.addConfiguration("mixins." + Constants.MOD_ID + ".json");

        return null;
    }
}