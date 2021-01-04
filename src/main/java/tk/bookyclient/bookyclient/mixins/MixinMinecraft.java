package tk.bookyclient.bookyclient.mixins;
// Created by booky10 in bookyClient (15:13 01.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.features.WindowedFullscreen;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow
    private boolean fullscreen;

    @Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", remap = false))
    private void onDisplaySetTitle(String title) {
        Display.setTitle(Constants.MOD_NAME + " " + Constants.VERSION + " (" + title + ")");
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void afterInit(GameConfiguration gameConfig, CallbackInfo callbackInfo) {
        WindowedFullscreen.makeWindowed(fullscreen);
    }

    @Inject(method = "toggleFullscreen", at = @At("RETURN"))
    public void afterToggleFullscreen(CallbackInfo callbackInfo) {
        WindowedFullscreen.makeWindowed(fullscreen);
    }
}