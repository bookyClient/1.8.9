package tk.bookyclient.bookyclient.mixins.client;
// Created by booky10 in bookyClient (19:58 14.04.21)

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"), expect = 2)
    public boolean onSprintKeyGet(KeyBinding key) {
        return key.isKeyDown() || ClientSettings.getInstance().toggleSprint;
    }
}