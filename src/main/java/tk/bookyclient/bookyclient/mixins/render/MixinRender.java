package tk.bookyclient.bookyclient.mixins.render;
// Created by booky10 in bookyClient (18:51 15.04.21)

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {

    @Shadow @Final protected RenderManager renderManager;

    @Inject(method = "renderLivingLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/Tessellator;getInstance()Lnet/minecraft/client/renderer/Tessellator;", shift = At.Shift.BEFORE), cancellable = true)
    public void onBackgroundRenderStart(T entity, String name, double x, double y, double z, int maxDistance, CallbackInfo callbackInfo) {
        FontRenderer fontrenderer = renderManager.getFontRenderer();
        int height = name.equals("deadmau5") ? -10 : 0;

        if (ClientSettings.getInstance().nameTagBackground) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer renderer = tessellator.getWorldRenderer();

            int posX = fontrenderer.getStringWidth(name) / 2;
            GlStateManager.disableTexture2D();

            renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            renderer.pos(-posX - 1, -1 + height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            renderer.pos(-posX - 1, 8 + height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            renderer.pos(posX + 1, 8 + height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            renderer.pos(posX + 1, -1 + height, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();

            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, height, 553648127);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, height, -1);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        callbackInfo.cancel();
    }
}