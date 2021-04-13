package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (22:44 04.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends GuiIngame {

    @SuppressWarnings("unused")
    public MixinGuiIngameForge(Minecraft mcIn) {
        super(mcIn);
    }

    @Redirect(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isIntegratedServerRunning()Z"))
    public boolean onIntegratedRunning(Minecraft minecraft) {
        return false;
    }
}