package tk.bookyclient.bookyclient.mixins.main;
// Created by booky10 in bookyClient (10:33 15.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
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
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tk.bookyclient.bookyclient.features.CrashScreenGUI;
import tk.bookyclient.bookyclient.utils.Constants;
import tk.bookyclient.bookyclient.utils.StateManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    @Shadow @SuppressWarnings("RedundantThrows") protected abstract void startGame();

    @Shadow @SuppressWarnings("RedundantThrows") protected abstract void runGameLoop();

    @Shadow public abstract void displayGuiScreen(GuiScreen guiScreenIn);

    @Shadow public abstract CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash);

    @Shadow public abstract void shutdownMinecraftApplet();

    @Shadow public abstract NetHandlerPlayClient getNetHandler();

    @Shadow public abstract void loadWorld(WorldClient worldClientIn);

    @Shadow public abstract void refreshResources();

    @Shadow public abstract void updateDisplay();

    @Shadow protected abstract void checkGLError(String message);

    private int crashCount = 0;

    /**
     * @author booky10
     * @reason Deny these actually crashing crashes
     */
    @Overwrite(remap = false)
    public void run() {
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
    }

    public void addInfoToCrash(CrashReport report) {
        report.getCategory().addCrashSection("Crashes Since Start", crashCount);
    }

    public void displayInitCrash(CrashReport report) {
        Constants.UTILITIES.saveReport(report);

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
                // noinspection deprecation
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
            Constants.UTILITIES.saveReport(report);

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

    /**
     * @author booky10
     * @reason My methods are better
     */
    @Overwrite(remap = false)
    public void displayCrashReport(CrashReport report) {
        Constants.UTILITIES.saveReport(report);
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

            Constants.UTILITIES.resetState();

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
                Constants.UTILITIES.resetState();
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * @author booky10
     * @reason My methods still better
     */
    @Overwrite(remap = false)
    public void freeMemory() {
        resetGameState();
    }
}