package tk.bookyclient.bookyclient.mixins.entity;
// Created by booky10 in bookyClient (14:15 09.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

    private static final ClientSettings settings = ClientSettings.getInstance();

    @Shadow private Minecraft mc;

    @Inject(method = "getFOVModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;", shift = At.Shift.BEFORE), cancellable = true)
    public void afterUseCheck(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (!useFOVSetting) return;
        if (ClientSettings.zoom) callbackInfoReturnable.setReturnValue(30f);
        else if (!settings.fovModifier) callbackInfoReturnable.setReturnValue(mc.gameSettings.fovSetting);
    }
}