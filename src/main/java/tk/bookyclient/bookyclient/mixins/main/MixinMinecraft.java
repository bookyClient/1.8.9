package tk.bookyclient.bookyclient.mixins.main;
// Created by booky10 in bookyClient (15:13 01.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.ClientMod;
import tk.bookyclient.bookyclient.features.WindowedFullscreen;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    private IntBuffer pixelBuffer;
    private int[] pixelValues;

    @Shadow private boolean fullscreen;

    @Shadow @Final public Profiler mcProfiler;

    @Shadow
    public abstract String getVersion();

    @Shadow public WorldClient theWorld;

    @Shadow public EntityRenderer entityRenderer;

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

    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ScreenShotHelper;saveScreenshot(Ljava/io/File;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;"))
    public IChatComponent onScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
        try {
            if (OpenGlHelper.isFramebufferEnabled()) {
                width = buffer.framebufferTextureWidth;
                height = buffer.framebufferTextureHeight;
            }

            int size = width * height;
            if (pixelBuffer == null || pixelBuffer.capacity() < size) {
                pixelBuffer = BufferUtils.createIntBuffer(size);
                pixelValues = new int[size];
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            pixelBuffer.clear();

            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer.framebufferTexture);
                GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            } else {
                GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
            }

            pixelBuffer.get(pixelValues);
            TextureUtil.processPixelValues(pixelValues, width, height);
            AtomicReference<BufferedImage> image = new AtomicReference<>();

            String date = Constants.DATE_FORMAT.format(new Date());
            int count = 1;

            File file;
            while (true) {
                file = new File(Constants.SCREENSHOT_DIR, date + (count == 1 ? "" : "_" + count) + ".png");
                if (!file.exists()) break;
                ++count;
            }

            File finalFile = file;
            int finalWidth = width;
            int finalHeight = height;
            boolean async = ClientSettings.getInstance().asyncScreenshots;

            Runnable runnable = () -> {
                if (OpenGlHelper.isFramebufferEnabled()) {
                    image.set(new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1));
                    int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

                    for (int k = j; k < buffer.framebufferTextureHeight; ++k) {
                        for (int l = 0; l < buffer.framebufferWidth; ++l) {
                            image.get().setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
                        }
                    }
                } else {
                    image.set(new BufferedImage(finalWidth, finalHeight, 1));
                    image.get().setRGB(0, 0, finalWidth, finalHeight, pixelValues, 0, finalWidth);
                }

                try {
                    ImageIO.write(image.get(), "png", finalFile);
                    if (!async) return;

                    IChatComponent component = new ChatComponentText(finalFile.getName());
                    component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, finalFile.getAbsolutePath()));
                    component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(I18n.format("screenshot.open"))));
                    component.getChatStyle().setUnderlined(true);

                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentTranslation("screenshot.success", component));
                } catch (IOException exception) {
                    exception.printStackTrace();
                    Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentTranslation("screenshot.failure", exception.toString()));
                }
            };

            if (async) {
                new Thread(runnable).start();
                return new ChatComponentTranslation("client.async_screenshots.started");
            } else {
                runnable.run();

                IChatComponent component = new ChatComponentText(file.getName());
                component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
                component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentTranslation("screenshot.open")));
                component.getChatStyle().setUnderlined(true);

                return new ChatComponentTranslation("screenshot.success", component);
            }
        } catch (Exception exception) {
            Constants.LOGGER.warn("Couldn't save screenshot", exception);
            return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
        }
    }

    @Inject(method = "displayGuiScreen", at = @At("RETURN"))
    public void postGUIOpen(GuiScreen screen, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().blurGuiBackground || theWorld == null) return;
        boolean excluded = screen == null || screen instanceof GuiChat;

        if (!entityRenderer.isShaderActive() && !excluded) {
            entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            Constants.UTILITIES.setBlurStart();
        } else if (entityRenderer.isShaderActive() && excluded) {
            entityRenderer.stopUseShader();
        }
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    public void postTick(CallbackInfo callbackInfo) {
        Constants.UTILITIES.tick();
    }
}