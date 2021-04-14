package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (22:35 04.01.21)

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(GuiChat.class)
public class MixinGuiChat extends GuiScreen {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        KeystrokesUtils.renderer.renderKeystrokes();
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiChat;drawRect(IIIII)V"))
    public void onDrawRect(int left, int top, int right, int bottom, int color) {
        if (ClientSettings.getInstance().clearChatBar) return;
        drawRect(2, height - 14, width - 2, height - 2, Integer.MIN_VALUE);
    }
}