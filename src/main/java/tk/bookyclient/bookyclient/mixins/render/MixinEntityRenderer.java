package tk.bookyclient.bookyclient.mixins.render;
// Created by booky10 in bookyClient (14:15 09.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

    @Shadow private Minecraft mc;

    @Inject(method = "getFOVModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;", shift = At.Shift.BEFORE), cancellable = true)
    public void afterUseCheck(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> returnValue) {
        if (!useFOVSetting) return;

        if (ClientSettings.zoom) {
            returnValue.setReturnValue(30f);
        } else if (!ClientSettings.getInstance().fovModifier) {
            returnValue.setReturnValue(mc.gameSettings.fovSetting);
        }
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
    public float onRotationYaw(Entity entity) {
        return ClientSettings.perspective ? Constants.UTILITIES.getCameraYaw() : entity.rotationYaw;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationPitch:F"))
    public float onRotationPitch(Entity entity) {
        return ClientSettings.perspective ? Constants.UTILITIES.getCameraPitch() : entity.rotationPitch;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationYaw:F"))
    public float onPrevRotationYaw(Entity entity) {
        return ClientSettings.perspective ? Constants.UTILITIES.getCameraYaw() : entity.prevRotationYaw;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;prevRotationPitch:F"))
    public float onPrevRotationPitch(Entity entity) {
        return ClientSettings.perspective ? Constants.UTILITIES.getCameraPitch() : entity.prevRotationPitch;
    }

    @Redirect(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;inGameHasFocus:Z"))
    public boolean onFocusGet(Minecraft minecraft) {
        if (minecraft.inGameHasFocus && Display.isActive()) {
            if (!ClientSettings.perspective) return true;

            minecraft.mouseHelper.mouseXYChange();
            float f1 = minecraft.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            float f3 = (float) minecraft.mouseHelper.deltaX * f2;
            float f4 = (float) minecraft.mouseHelper.deltaY * f2;

            Constants.UTILITIES.setCameraYaw(Constants.UTILITIES.getCameraYaw() + f3 * 0.15f);
            Constants.UTILITIES.setCameraPitch(Constants.UTILITIES.getCameraPitch() + f4 * 0.15f);

            if (Math.abs(Constants.UTILITIES.getCameraPitch()) > 90) {
                Constants.UTILITIES.setCameraPitch(Constants.UTILITIES.getCameraPitch() > 0.0f ? 90.0f : -90.0f);
            }
        }

        return false;
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;clear(I)V", ordinal = 1))
    public void preHandRender(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().oldPunching || mc.thePlayer.getItemInUseCount() <= 0) return;
        if (!mc.gameSettings.keyBindAttack.isKeyDown() || !mc.gameSettings.keyBindUseItem.isKeyDown()) return;
        if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;
        if (mc.thePlayer.isSwingInProgress && mc.thePlayer.swingProgressInt < (mc.thePlayer.isPotionActive(Potion.digSpeed) ? 6 - 1 + mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() : mc.thePlayer.isPotionActive(Potion.digSlowdown) ? 6 + (1 + mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6) / 2 && mc.thePlayer.swingProgress >= 0) return;

        mc.thePlayer.swingProgressInt = -1;
        mc.thePlayer.isSwingInProgress = true;
    }
}