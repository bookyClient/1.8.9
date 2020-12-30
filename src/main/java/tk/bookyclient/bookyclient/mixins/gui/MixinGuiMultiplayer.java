package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (16:12 30.12.20)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public class MixinGuiMultiplayer extends GuiScreen implements GuiYesNoCallback {

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        switch (mc.getSession().getToken()) {
            case "0":
            case "FML":
                drawCenteredString(Minecraft.getMinecraft().fontRendererObj, I18n.format("accounts.offline"), width / 2, 10, 16737380);
                break;
            default:
                break;
        }
    }
}