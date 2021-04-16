package tk.bookyclient.bookyclient.mixins.render;
// Created by booky10 in bookyClient (16:43 16.04.21)

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.features.betterhead.ModelBoxUV;
import tk.bookyclient.bookyclient.features.betterhead.ModelRendererUV;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(ModelBiped.class)
public class MixinModelBiped extends ModelBase {

    @Shadow public ModelRenderer bipedHeadwear;

    @Inject(method = "render", at = @At("HEAD"))
    public void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().betterHead) return;

        if (bipedHeadwear instanceof ModelRendererUV) {
            GlStateManager.resetColor();
            GlStateManager.enableDepth();

            bipedHeadwear.isHidden = false;
            if (entity.isSneaking()) GlStateManager.translate(0, 0.2, 0);

            ((ModelRendererUV) bipedHeadwear).applyRotation(bipedHeadwear);
            ((ModelRendererUV) bipedHeadwear).renderBetterHat(scale);

            bipedHeadwear.isHidden = true;
            if (entity.isSneaking()) GlStateManager.translate(0, -0.2, 0);
        } else {
            ModelRendererUV model = new ModelRendererUV(this, 32, 0);
            ModelBoxUV box = null;

            for (int x = -4; x < 4; ++x) {
                for (int z = -4; z < 4; ++z) {
                    box = ModelBoxUV.addBox(model, x * 1.135f, -8.5225f, z * 1.135f, 1, 1, 1, 0.001f + 0.07f);
                    box.setAllUV(44 + x, 3 - z);
                    (box = ModelBoxUV.addBox(model, x * 1.135f, -0.5575f, z * 1.135f, 1, 1, 1, 0.001f + 0.07f)).setAllUV(52 + x, 3 - z);
                }
            }

            for (int x = -4; x < 4; ++x) {
                for (int y = -8; y < 0; ++y) {
                    box = ModelBoxUV.addBox(model, x * 1.135f, (y + 0.5f) * 1.135f, -4.55f, 1, 1, 1, 0.001f + 0.07f);
                    box.setAllUV(44 + x, 16 + y);
                    (box = ModelBoxUV.addBox(model, x * 1.135f, (y + 0.5f) * 1.135f, 3.415f, 1, 1, 1, 0.001f + 0.07f)).setAllUV(60 + x, 16 + y);
                }
            }

            for (int z = -3; z < 4; ++z) {
                for (int y = -8; y < 0; ++y) {
                    box = ModelBoxUV.addBox(model, -4.55f, (y + 0.5f) * 1.135f, z * 1.135f, 1, 1, 1, 0.001f + 0.07f);
                    box.setAllUV(36 - z - 1, 16 + y);
                    (box = ModelBoxUV.addBox(model, 3.415f, (y + 0.5f) * 1.135f, z * 1.135f, 1, 1, 1, 0.001f + 0.07f)).setAllUV(52 + z, 16 + y);
                }
            }

            box.initQuads();
            model.setRotationPoint(0, headPitch, 0);

            bipedHeadwear = model;
        }
    }
}