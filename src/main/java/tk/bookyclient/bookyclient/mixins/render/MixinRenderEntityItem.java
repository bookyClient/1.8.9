package tk.bookyclient.bookyclient.mixins.render;
// Created by booky10 in bookyClient (15:48 16.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.Random;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem extends Render<EntityItem> {

    @Shadow
    public abstract boolean shouldSpreadItems();

    private static final Random random = new Random();

    protected MixinRenderEntityItem(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    public void preRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().itemPhysics) return;

        double rotation = Minecraft.getMinecraft().inGameHasFocus ? (System.nanoTime() - Constants.UTILITIES.getTick()) / 2500000.0 * 0.25 : 0;
        ItemStack item = entity.getEntityItem();

        int id = item == null || item.getItem() == null ? 187 : Item.getIdFromItem(item.getItem()) + item.getMetadata();
        random.setSeed(id);

        bindTexture(TextureMap.locationBlocksTexture);
        getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();

        RenderHelper.enableStandardItemLighting();

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(item);
        boolean is3D = model.isGui3d();

        GlStateManager.translate(x, y, z);
        if (model.isGui3d()) GlStateManager.scale(0.5f, 0.5f, 0.5f);

        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(entity.rotationYaw, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(0.0, 0.0, is3D ? -0.08 : -0.04);

        if (is3D || Minecraft.getMinecraft().getRenderManager().options != null) {
            if (is3D) {
                if (!entity.onGround) {
                    entity.rotationPitch += (float) rotation * 2.0;
                }
            } else if (!Double.isNaN(entity.posX) && !Double.isNaN(entity.posY) && !Double.isNaN(entity.posZ) && entity.worldObj != null) {
                if (entity.onGround) {
                    entity.rotationPitch = 0.0f;
                } else {
                    entity.rotationPitch += (float) rotation * 2.0;
                }
            }
            GlStateManager.rotate(entity.rotationPitch, 1.0f, 0.0f, 0.0f);
        }

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        for (int k = 0; k < getModelCount(item); ++k) {
            GlStateManager.pushMatrix();
            if (is3D) {
                if (k > 0) {
                    float f4 = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f5 = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float f6 = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    GlStateManager.translate(shouldSpreadItems() ? f4 : 0.0f, shouldSpreadItems() ? f5 : 0.0f, f6);
                }
                Minecraft.getMinecraft().getRenderItem().renderItem(item, model);
                GlStateManager.popMatrix();
            } else {
                Minecraft.getMinecraft().getRenderItem().renderItem(item, model);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0f, 0.0f, 0.05375f);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();

        bindTexture(TextureMap.locationBlocksTexture);
        getRenderManager().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();

        callbackInfo.cancel();
    }

    private int getModelCount(ItemStack item) {
        int count = 1;

        if (item != null) {
            if (item.stackSize > 48) {
                count = 5;
            } else if (item.stackSize > 32) {
                count = 4;
            } else if (item.stackSize > 16) {
                count = 3;
            } else if (item.stackSize > 1) {
                count = 2;
            }
        }

        return count;
    }
}