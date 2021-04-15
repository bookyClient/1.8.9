package tk.bookyclient.bookyclient.mixins.forge.gui;
// Created by booky10 in bookyClient (17:38 13.04.21)

import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.ArrayList;

@Mixin(GuiModList.class)
public class MixinGuiModList {

    @Shadow(remap = false) private ArrayList<ModContainer> mods;

    @Inject(method = "reloadMods", at = @At(value = "RETURN"), remap = false)
    public void afterReload(CallbackInfo callbackInfo) {
        mods.add(Constants.MOD_CONTAINER);
    }
}