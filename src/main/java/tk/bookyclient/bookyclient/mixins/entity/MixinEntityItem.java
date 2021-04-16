package tk.bookyclient.bookyclient.mixins.entity;
// Created by booky10 in bookyClient (15:48 16.04.21)

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(EntityItem.class)
public abstract class MixinEntityItem extends Entity {

    public MixinEntityItem(World world) {
        super(world);
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_) {
        if (ClientSettings.getInstance().itemPhysics) setPosition(x, y, z);
        else super.setPositionAndRotation2(x, y, z, yaw, pitch, posRotationIncrements, p_180426_10_);
    }
}