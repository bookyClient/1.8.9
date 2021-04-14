package tk.bookyclient.bookyclient.mixins.client;
// Created by booky10 in bookyClient (15:13 01.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.profiler.Profiler;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.ClientMod;
import tk.bookyclient.bookyclient.features.WindowedFullscreen;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow private boolean fullscreen;

    @Shadow @Final public Profiler mcProfiler;

    @Shadow public abstract String getVersion();

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;createDisplay()V", shift = At.Shift.AFTER, by = 1))
    private void onDisplaySetTitle(CallbackInfo callbackInfo) {
        Display.setTitle(Constants.MOD_NAME + " " + Constants.VERSION + " (" + getVersion() + ")");
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void afterInit(GameConfiguration gameConfig, CallbackInfo callbackInfo) {
        WindowedFullscreen.makeWindowed(fullscreen);
    }

    @Inject(method = "toggleFullscreen", at = @At("RETURN"))
    public void afterToggleFullscreen(CallbackInfo callbackInfo) {
        WindowedFullscreen.makeWindowed(fullscreen);
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;updateAchievementWindow()V", shift = At.Shift.BY, by = -1))
    public void onRender(CallbackInfo callbackInfo) {
        mcProfiler.startSection("bookyClient");

        mcProfiler.endStartSection("Rendering Keystrokes");
        KeystrokesUtils.renderer.tryRender();

        mcProfiler.endSection();
        mcProfiler.endSection();
    }

    @Inject(method = "startGame", at = @At("HEAD"))
    public void preStart(CallbackInfo callbackInfo) {
        ClientMod.preStart((MinecraftAccessor) Minecraft.getMinecraft());
    }

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;textureMapBlocks:Lnet/minecraft/client/renderer/texture/TextureMap;", shift = At.Shift.BEFORE, ordinal = 0))
    public void start(CallbackInfo callbackInfo) {
        ClientMod.start();
    }

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.BEFORE))
    public void postStart(CallbackInfo callbackInfo) {
        ClientMod.postStart();
    }

    @ModifyConstant(method = "getLimitFramerate", constant = @Constant(intValue = 30))
    public int onMenuFrameLimit(int original) {
        return 60;
    }
}