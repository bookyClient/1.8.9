package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (13:52 07.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(GuiIngame.class)
public class MixinGuiIngame extends Gui {

    private static final ClientSettings settings = ClientSettings.getInstance();

    @Shadow
    @Final
    protected Minecraft mc;

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    public void onShowCrosshair(CallbackInfoReturnable<Boolean> returnable) {
        if (mc.gameSettings.thirdPersonView != 0) returnable.setReturnValue(false);
    }

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;drawRect(IIIII)V"))
    public void onDrawRect(int left, int top, int right, int bottom, int color) {
        if (settings.clearScoreboardBackground) return;
        drawRect(left, top, right, bottom, color);
    }
}