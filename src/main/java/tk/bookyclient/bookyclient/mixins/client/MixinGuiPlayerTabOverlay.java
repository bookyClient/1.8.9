package tk.bookyclient.bookyclient.mixins.client;
// Created by booky10 in bookyClient (18:21 15.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinGuiPlayerTabOverlay extends Gui {

    @Shadow @Final private Minecraft mc;

    @Inject(method = "drawPing", at = @At("HEAD"), cancellable = true)
    public void onDrawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo player, CallbackInfo callbackInfo) {
        if (player.getResponseTime() != 1) return;
        callbackInfo.cancel();
    }

    @Inject(method = "drawPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void onTextureBind(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo player, CallbackInfo callbackInfo) {
        int ping = player.getResponseTime(), texture = ping < 0 ? 5 : ping < 150 ? 0 : ping < 300 ? 1 : ping < 600 ? 2 : ping < 1000 ? 3 : 4;

        zLevel += 100;
        if (ClientSettings.getInstance().numeralPing) {
            drawString(mc.fontRendererObj, Integer.toString(ping), p_175245_2_ + p_175245_1_ - 11, p_175245_3_, Constants.PING_COLORS.get(texture));
        } else {
            mc.getTextureManager().bindTexture(icons);
            drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0, 176 + texture * 8, 10, 8);
        }
        zLevel -= 100;

        callbackInfo.cancel();
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V"), expect = 4)
    public void onRectangleDraw(int left, int top, int right, int bottom, int color) {
        if (ClientSettings.getInstance().invisibleTablist) return;
        drawRect(left, top, right, bottom, color);
    }
}