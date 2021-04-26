package tk.bookyclient.bookyclient.mixins.render;
// Created by booky10 in bookyClient (21:09 26.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Shadow private float prevEquippedProgress;
    @Shadow private float equippedProgress;
    @Shadow @Final private Minecraft mc;
    @Shadow private ItemStack itemToRender;

    @Shadow
    protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);


    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    @SuppressWarnings("deprecation")
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    @SuppressWarnings("deprecation")
    @Inject(method = "renderItemInFirstPerson", at = @At(value = "HEAD"), cancellable = true)
    public void onBlockHitRender(float partialTicks, CallbackInfo callbackInfo) {
        float f = 1.0F - (prevEquippedProgress + (equippedProgress - prevEquippedProgress) * partialTicks);
        EntityPlayerSP player = mc.thePlayer;

        float f1 = player.getSwingProgress(partialTicks);
        float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        rotateArroundXAndY(f2, f3);
        setLightMapFromPlayer(player);
        rotateWithPlayerRotations(player, partialTicks);

        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (itemToRender != null) {
            if (itemToRender.getItem() instanceof net.minecraft.item.ItemMap) {
                renderItemMap(player, f2, f, f1);
            } else if (player.getItemInUseCount() > 0) {
                EnumAction action = itemToRender.getItemUseAction();

                switch (action) {
                    case NONE:
                        transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        performDrinking(player, partialTicks);
                        transformFirstPersonItem(f, ClientSettings.getInstance().oldPunching ? f1 : 0.0F);
                        break;
                    case BLOCK:
                        transformFirstPersonItem(f, ClientSettings.getInstance().oldBlockHitting ? f1 : 0.0F);
                        doBlockTransformations();
                        break;
                    case BOW:
                        transformFirstPersonItem(f, 0.0F);
                        doBowTransformations(partialTicks, player);
                        break;
                    default:
                        break;
                }
            } else {
                doItemUsedTransformations(f1);
                transformFirstPersonItem(f, f1);
            }

            renderItem(player, itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!player.isInvisible()) {
            renderPlayerArm(player, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();

        callbackInfo.cancel();
    }
}