package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (22:35 04.01.21)

import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        KeystrokesUtils.renderer.renderKeystrokes();
    }
}