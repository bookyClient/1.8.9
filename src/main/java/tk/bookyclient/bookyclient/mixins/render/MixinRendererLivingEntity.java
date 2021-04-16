package tk.bookyclient.bookyclient.mixins.render;
// Created by booky10 in bookyClient (23:39 16.04.21)

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity {

    @Redirect(method = "canRenderName", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/RenderManager;livingPlayer:Lnet/minecraft/entity/Entity;"))
    public Entity onPlayerGet(RenderManager renderManager) {
        return ClientSettings.getInstance().renderOwnName ? null : renderManager.livingPlayer;
    }
}