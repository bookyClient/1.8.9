package tk.bookyclient.bookyclient.mixins;
// Created by booky10 in bookyClient (22:13 04.01.21)

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesTracker;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "hitByEntity", at = @At("RETURN"))
    public void onHit(Entity entityIn, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        KeystrokesTracker.updateReach();
    }
}