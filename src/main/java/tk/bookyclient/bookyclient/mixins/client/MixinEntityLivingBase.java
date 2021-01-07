package tk.bookyclient.bookyclient.mixins.client;
// Created by booky10 in bookyClient (15:34 07.01.21)

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    @SuppressWarnings("unused")
    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
    public void onGetLook(float partialTicks, CallbackInfoReturnable<Vec3> returnable) {
        if (getClass().getName().equals(EntityPlayerSP.class.getName()))
            returnable.setReturnValue(super.getLook(partialTicks));
    }
}