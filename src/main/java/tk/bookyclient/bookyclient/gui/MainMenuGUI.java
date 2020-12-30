package tk.bookyclient.bookyclient.gui;
// Created by booky10 in bookyClient (21:17 29.12.20)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GLContext;
import tk.bookyclient.bookyclient.accounts.gui.AccountSelectorGUI;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.Random;

public class MainMenuGUI extends GuiScreen implements GuiYesNoCallback {

    private static final Random random = new Random();
    private final Object lock = new Object();
    private final Float updateCounter;
    private String openGLWarning;

    private static final ResourceLocation backgroundTexture = new ResourceLocation("bookyClient", "splash/background.png");
    private static final ResourceLocation titleTexture = new ResourceLocation("minecraft", "textures/gui/title/minecraft.png");

    private Integer finalX, finalY;
    private static MainMenuGUI instance;

    private MainMenuGUI() {
        if (mc == null) mc = Minecraft.getMinecraft();

        updateCounter = random.nextFloat();
        openGLWarning = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported())
            openGLWarning = I18n.format("title.oldgl1");
    }

    public static MainMenuGUI getInstance() {
        if (instance == null) instance = new MainMenuGUI();
        return instance;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void initGui() {
        int height = this.height / 4 + 48;

        buttonList.clear();
        buttonList.add(new GuiButton(1, width / 2 - 100, height, I18n.format("menu.singleplayer")));
        buttonList.add(new GuiButton(2, width / 2 - 100, height + 24, I18n.format("menu.multiplayer")));
        buttonList.add(new GuiButton(0, width / 2 - 100, height + 48, I18n.format("menu.options")));
        buttonList.add(new GuiButton(6, width / 2 - 100, height + 72, "Mods"));
        buttonList.add(new GuiButton(4, width / 2 - 100, height + 96, I18n.format("menu.quit")));

        buttonList.add(new GuiButton(Constants.ACCOUNTS_BUTTON_ID, width / 2 - 100, height - 24, "Accounts"));

        synchronized (lock) {
            int width = fontRendererObj.getStringWidth(openGLWarning);
            int x = (this.width - width) / 2;
            int y = buttonList.get(0).yPosition - 24;
            finalX = x + width;
            finalY = y + 24;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0)
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        else if (button.id == 1)
            mc.displayGuiScreen(new GuiSelectWorld(this));
        else if (button.id == 2)
            mc.displayGuiScreen(new GuiMultiplayer(this));
        else if (button.id == 4)
            mc.shutdown();
        else
            mc.displayGuiScreen(new AccountSelectorGUI());
    }

    private void renderBackground() {
        ScaledResolution resolution = new ScaledResolution(mc);

        mc.getTextureManager().bindTexture(backgroundTexture);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, resolution.getScaledWidth(), resolution.getScaledHeight(), 1920, 1080);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        renderBackground();
        GlStateManager.enableAlpha();

        int x = width / 2 - 274 / 2;
        int y = 30;

        mc.getTextureManager().bindTexture(titleTexture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double) updateCounter < 1.0E-4D) {
            drawTexturedModalRect(x, y, 0, 0, 99, 44);
            drawTexturedModalRect(x + 99, y, 129, 0, 27, 44);
            drawTexturedModalRect(x + 99 + 26, y, 126, 0, 3, 44);
            drawTexturedModalRect(x + 99 + 26 + 3, y, 99, 0, 26, 44);
            drawTexturedModalRect(x + 155, y, 0, 45, 155, 44);
        } else {
            drawTexturedModalRect(x, y, 0, 0, 155, 44);
            drawTexturedModalRect(x + 155, y, 0, 45, 155, 44);
        }

        String version = "bookyClient " + Constants.VERSION + " (" + Minecraft.getMinecraft().getVersion() + ")";

        drawString(fontRendererObj, version, 2, height - 10, -1);

        if (openGLWarning != null && openGLWarning.length() > 0) {
            drawRect(x - 2, y - 2, finalX + 2, finalY - 1, 0x55200000);
            drawString(fontRendererObj, openGLWarning, x, y, -1);
        }

        drawCenteredString(mc.fontRendererObj, I18n.format("ias.loggedinas", mc.getSession().getUsername()), width / 2, height / 4 + 48 + 3 + 72 + 22, 0xFFCC8888);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}