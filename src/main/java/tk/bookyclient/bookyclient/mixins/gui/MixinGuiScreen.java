package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (20:03 15.04.21)

import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {

    @Redirect(method = "drawWorldBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawGradientRect(IIIIII)V"))
    public void onRectangleDraw(GuiScreen gui, int left, int top, int right, int bottom, int startColor, int endColor) {
        if (ClientSettings.getInstance().blurGuiBackground) {
            GuiScreen.drawRect(left, top, right, bottom, Constants.UTILITIES.getGUIBackgroundColor());
        } else {
            gui.drawGradientRect(left, top, right, bottom, startColor, endColor);
        }
    }
}