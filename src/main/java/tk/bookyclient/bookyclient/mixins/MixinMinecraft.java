package tk.bookyclient.bookyclient.mixins;
// Created by booky10 in bookyClient (15:13 01.01.21)

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", remap = false))
    private void onDisplaySetTitle(String title) {
        Display.setTitle(Constants.MOD_NAME + " " + Constants.VERSION + " (" + title + ")");
    }
}