package tk.bookyclient.bookyclient.core;
// Created by booky10 in bookyClient (21:40 29.12.20)

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class ClientFMLSetupHook implements IFMLCallHook {

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public Void call() {
        System.out.println("Injecting Mixins...");

        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.bookyclient.json");

        System.out.println("Successfully injected Mixins!");
        return null;
    }
}