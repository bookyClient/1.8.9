package tk.bookyclient.bookyclient.mixins.main;
// Created by booky10 in bookyClient (10:33 15.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.SplashProgress;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.features.CrashScreenGUI;
import tk.bookyclient.bookyclient.utils.Constants;
import tk.bookyclient.bookyclient.utils.StateManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.FutureTask;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftCrash {

    @Shadow volatile boolean running;
    @Shadow private boolean hasCrashed;
    @Shadow private CrashReport crashReporter;
    @Shadow private long debugCrashKeyPressTime;
    @Shadow public static byte[] memoryReserve;
    @Shadow public GameSettings gameSettings;
    @Shadow public EntityRenderer entityRenderer;
    @Shadow @Final private Queue<FutureTask<?>> scheduledTasks;
    @Shadow public GuiScreen currentScreen;
    @Shadow public int displayWidth;
    @Shadow public int displayHeight;
    @Shadow public TextureManager renderEngine;
    @Shadow public FontRenderer fontRendererObj;
    @Shadow private int leftClickCounter;
    @Shadow private Framebuffer framebufferMc;
    @Shadow private IReloadableResourceManager mcResourceManager;
    @Shadow private SoundHandler mcSoundHandler;
    @Shadow @Final private List<IResourcePack> defaultResourcePacks;
    @Shadow private LanguageManager mcLanguageManager;
    @Shadow @Final private IMetadataSerializer metadataSerializer_;

    @Shadow
    @SuppressWarnings("RedundantThrows")
    protected abstract void startGame();

    @Shadow
    @SuppressWarnings("RedundantThrows")
    protected abstract void runGameLoop();

    @Shadow
    public abstract void displayGuiScreen(GuiScreen guiScreenIn);

    @Shadow
    public abstract CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash);

    @Shadow
    public abstract void shutdownMinecraftApplet();

    @Shadow
    public abstract NetHandlerPlayClient getNetHandler();

    @Shadow
    public abstract void loadWorld(WorldClient worldClientIn);

    @Shadow
    public abstract void refreshResources();

    @Shadow
    public abstract void updateDisplay();

    @Shadow
    protected abstract void checkGLError(String message);

    @Shadow
    public abstract void displayCrashReport(CrashReport crashReportIn);

    @Shadow
    public abstract void freeMemory();

    private int crashCount = 0;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    public void run(CallbackInfo callbackInfo) {
        running = true;

        try {
            startGame();
        } catch (Throwable throwable) {
            CrashReport report = CrashReport.makeCrashReport(throwable, "Initializing game");
            displayInitCrash(addGraphicsAndWorldToCrashReport(report));
            return;
        }

        try {
            while (running) {
                if (!hasCrashed || crashReporter == null) {
                    try {
                        runGameLoop();
                    } catch (ReportedException exception) {
                        crashCount++;

                        addGraphicsAndWorldToCrashReport(exception.getCrashReport());
                        addInfoToCrash(exception.getCrashReport());
                        resetGameState();

                        Constants.LOGGER.error("Reported exception thrown!", exception);
                        displayCrashScreen(exception.getCrashReport());
                    } catch (Throwable throwable) {
                        crashCount++;

                        CrashReport report = new CrashReport("Unexpected error", throwable);
                        addGraphicsAndWorldToCrashReport(report);
                        addInfoToCrash(report);
                        resetGameState();

                        Constants.LOGGER.error("Unreported exception thrown!", throwable);
                        displayCrashScreen(report);
                    }
                } else {
                    crashCount++;

                    addInfoToCrash(crashReporter);
                    freeMemory();
                    displayCrashScreen(crashReporter);

                    hasCrashed = false;
                    crashReporter = null;
                }
            }
        } catch (MinecraftError ignored) {
        } finally {
            shutdownMinecraftApplet();
        }

        callbackInfo.cancel();
    }

    public void addInfoToCrash(CrashReport report) {
        report.getCategory().addCrashSection("Crashes Since Start", crashCount);
    }

    @SuppressWarnings("deprecation")
    public void displayInitCrash(CrashReport report) {
        saveReport(report);

        try {
            try {
                URL url = Constants.class.getProtectionDomain().getCodeSource().getLocation();
                if (url.getProtocol().equals("jar")) url = new URL(url.getFile().substring(0, url.getFile().indexOf('!')));
                File modFile = new File(url.toURI());
                defaultResourcePacks.add(modFile.isDirectory() ? new FolderResourcePack(modFile) : new FileResourcePack(modFile));
            } catch (Throwable throwable) {
                Constants.LOGGER.error("Failed to load resource pack", throwable);
            }

            mcResourceManager = new SimpleReloadableResourceManager(metadataSerializer_);

            mcResourceManager.registerReloadListener((renderEngine = new TextureManager(mcResourceManager)));
            mcResourceManager.registerReloadListener((mcLanguageManager = new LanguageManager(metadataSerializer_, gameSettings.language)));
            mcResourceManager.registerReloadListener((fontRendererObj = new FontRenderer(gameSettings, new ResourceLocation("textures/font/ascii.png"), renderEngine, false)));
            mcResourceManager.registerReloadListener((mcSoundHandler = new SoundHandler(mcResourceManager, gameSettings)));

            refreshResources();
            running = true;

            try {
                SplashProgress.pause();
            } catch (Throwable ignored) {
            }

            runGUILoop(new CrashScreenGUI(report, -1));
        } catch (Throwable throwable) {
            Constants.LOGGER.error("An uncaught exception occurred while displaying the init crash report, crashing the game now", throwable);
            displayCrashReport(report);
            System.exit(report.getFile() != null ? -1 : -2);
        }
    }

    private void runGUILoop(GuiScreen screen) throws IOException {
        displayGuiScreen(screen);

        while (running && currentScreen != null && !(currentScreen instanceof GuiMainMenu)) {
            if (Display.isCreated() && Display.isCloseRequested()) System.exit(0);

            leftClickCounter = 10000;
            currentScreen.handleInput();
            currentScreen.updateScreen();

            GlStateManager.pushMatrix();
            GlStateManager.clear(16640);
            framebufferMc.bindFramebuffer(true);
            GlStateManager.enableTexture2D();

            GlStateManager.viewport(0, 0, displayWidth, displayHeight);

            ScaledResolution scaledResolution = new ScaledResolution((Minecraft) (Object) this);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0, 1000, 3000);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0, 0, -2000);
            GlStateManager.clear(256);

            int width = scaledResolution.getScaledWidth();
            int height = scaledResolution.getScaledHeight();
            int mouseX = Mouse.getX() * width / displayWidth;
            int mouseY = height - Mouse.getY() * height / displayHeight - 1;
            currentScreen.drawScreen(mouseX, mouseY, 0);

            framebufferMc.unbindFramebuffer();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            framebufferMc.framebufferRender(displayWidth, displayHeight);
            GlStateManager.popMatrix();

            updateDisplay();
            Thread.yield();
            Display.sync(60);

            checkGLError("Custom GUI Loop");
        }
    }

    public void displayCrashScreen(CrashReport report) {
        try {
            saveReport(report);

            hasCrashed = false;
            debugCrashKeyPressTime = -1;
            gameSettings.showDebugInfo = false;

            runGUILoop(new CrashScreenGUI(report, -1));
        } catch (Throwable throwable) {
            Constants.LOGGER.error("The crash report gui crashed LMAO", throwable);
            displayCrashReport(report);
            System.exit(report.getFile() != null ? -1 : -2);
        }
    }

    @Inject(method = "displayCrashReport", at = @At("HEAD"), cancellable = true)
    public void displayCrashReport(CrashReport report, CallbackInfo callbackInfo) {
        saveReport(report);
        callbackInfo.cancel();
    }

    public void resetGameState() {
        try {
            int originalMemoryReserveSize = -1;

            try {
                if (memoryReserve != null) {
                    originalMemoryReserveSize = memoryReserve.length;
                    memoryReserve = new byte[0];
                }
            } catch (Throwable ignored) {
            }

            StateManager.resetStates();
            if (getNetHandler() != null) getNetHandler().getNetworkManager().closeChannel(new ChatComponentText("Client crashed"));

            loadWorld(null);
            if (entityRenderer.isShaderActive()) entityRenderer.stopUseShader();
            scheduledTasks.clear();

            resetState();

            if (originalMemoryReserveSize != -1) {
                try {
                    memoryReserve = new byte[originalMemoryReserveSize];
                } catch (Throwable ignored) {
                }
            }

            System.gc();
        } catch (Throwable throwable) {
            Constants.LOGGER.error("Failed to reset state after a crash", throwable);

            try {
                StateManager.resetStates();
                resetState();
            } catch (Throwable ignored) {
            }
        }
    }

    @Inject(method = "freeMemory", at = @At("HEAD"), cancellable = true)
    public void freeMemory(CallbackInfo callbackInfo) {
        resetGameState();
        callbackInfo.cancel();
    }

    private void saveReport(CrashReport report) {
        try {
            if (report.getFile() == null) {
                File file = new File(Constants.CRASH_REPORT_DIR, String.format("crash-%s-client.txt", Constants.DATE_FORMAT.format(new Date())));
                report.saveToFile(file);
            } else {
                report.saveToFile(report.getFile());
            }
        } catch (Throwable throwable) {
            Constants.LOGGER.error("Failed saving report", throwable);
        }
    }

    /**
     * This is not written be me. Licensed under MIT License,
     * click on the link for the original repository.
     *
     * @author https://github.com/DimensionalDevelopment/VanillaFix
     */
    private void resetState() {
        try {
            // Clear matrix stack
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_COLOR);
            GlStateManager.loadIdentity();

            // Reset texture
            GlStateManager.bindTexture(0);
            GlStateManager.disableTexture2D();

            // Reset GL lighting
            GlStateManager.disableLighting();
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));
            for (int i = 0; i < 8; ++i) {
                GlStateManager.disableLight(i);
                GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_POSITION, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));

                if (i == 0) {
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                } else {
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GL11.glLight(GL11.GL_LIGHT0 + i, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                }
            }
            GlStateManager.disableColorMaterial();
            GlStateManager.colorMaterial(1032, 5634);

            // Reset depth
            GlStateManager.disableDepth();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);

            // Reset blend mode
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(1, 0);
            GlStateManager.tryBlendFuncSeparate(1, 0, 1, 0);
            GL14.glBlendEquation(GL14.GL_FUNC_ADD);

            // Reset fog
            GlStateManager.disableFog();
            GlStateManager.setFog(9729);
            GlStateManager.setFogDensity(1.0F);
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(1.0F);
            GL11.glFog(GL11.GL_FOG_COLOR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            if (GLContext.getCapabilities().GL_NV_fog_distance) GL11.glFogi(GL11.GL_FOG_MODE, 34140);

            // Reset polygon offset
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();

            // Reset color logic
            GlStateManager.disableColorLogic();
            GlStateManager.colorLogicOp(5379);

            // Reset texgen
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
            GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGen(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGen(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGen(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

            // Disable lightmap
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();

            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

            // Reset texture parameters
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 1000);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, 1000);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, -1000);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);

            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL11.GL_MODULATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_RGB, GL11.GL_TEXTURE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_RGB, GL13.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_RGB, GL13.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC0_ALPHA, GL11.GL_TEXTURE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC1_ALPHA, GL13.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL15.GL_SRC2_ALPHA, GL13.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_ALPHA, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL13.GL_RGB_SCALE, 1.0F);
            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_ALPHA_SCALE, 1.0F);

            GlStateManager.disableNormalize();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableRescaleNormal();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GL11.glLineWidth(1.0F);
            GL11.glNormal3f(0.0F, 0.0F, 1.0F);
            GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
            GL11.glPolygonMode(GL11.GL_BACK, GL11.GL_FILL);

            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(515);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.cullFace(1029);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }
}