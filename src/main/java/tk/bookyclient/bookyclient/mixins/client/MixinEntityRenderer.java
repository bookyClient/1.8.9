package tk.bookyclient.bookyclient.mixins.client;
// Created by booky10 in bookyClient (14:15 09.01.21)

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

    private static final ClientSettings settings = ClientSettings.getInstance();

    @Shadow
    private boolean debugView;

    @Shadow
    private Minecraft mc;

    @Shadow
    private float fovModifierHandPrev;

    @Shadow
    private float fovModifierHand;

    /**
     * @author booky10
     */
    @Overwrite(remap = false)
    private float getFOVModifier(float partialTicks, boolean useFOVSetting) {
        if (debugView) {
            return 90;
        } else {
            Entity entity = mc.getRenderViewEntity();
            float fov = 70;

            if (useFOVSetting) {
                fov = ClientSettings.zoom ? 30 : mc.gameSettings.fovSetting;
                if (!settings.fovModifier) return fov;

                fov = fov * (fovModifierHandPrev + (fovModifierHand - fovModifierHandPrev) * partialTicks);
            }

            if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0) {
                float deadTicks = (float) ((EntityLivingBase) entity).deathTime + partialTicks;
                fov /= (1 - 500 / (deadTicks + 500)) * 2 + 1;
            }

            Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(mc.theWorld, entity, partialTicks);
            if (block.getMaterial() == Material.water) fov = fov * 60 / 70;

            return ForgeHooksClient.getFOVModifier(EntityRenderer.class.cast(this), entity, block, partialTicks, fov);
        }
    }
}