package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (21:47 29.12.20)

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.accounts.gui.AccountSelectorGUI;
import tk.bookyclient.bookyclient.utils.Constants;

import java.util.List;

@Mixin(GuiMainMenu.class)
public abstract class MixinGuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    private final ResourceLocation backgroundTexture = new ResourceLocation("bookyclient", "splash/background.png");

    @Shadow
    @Final
    private Object threadLock;

    @Shadow
    private int field_92023_s, field_92024_r, field_92022_t, field_92021_u, field_92020_v, field_92019_w;

    @Shadow
    private String openGLWarning1, openGLWarning2;

    @Shadow
    @Final
    private static ResourceLocation minecraftTitleTextures;

    @Shadow
    private float updateCounter;

    /**
     * @author booky10
     */
    @Overwrite
    public void initGui() {
        int height = this.height / 4 + 48;

        buttonList.clear();
        buttonList.add(new GuiButton(1, width / 2 - 100, height, I18n.format("menu.singleplayer")));
        buttonList.add(new GuiButton(2, width / 2 - 100, height + 24, I18n.format("menu.multiplayer")));
        buttonList.add(new GuiButton(0, width / 2 - 100, height + 48, I18n.format("menu.options")));
        buttonList.add(new GuiButton(6, width / 2 - 100, height + 72, "Mods"));
        buttonList.add(new GuiButton(4, width / 2 - 100, height + 96, I18n.format("menu.quit")));

        buttonList.add(new GuiButton(Constants.ACCOUNTS_BUTTON_ID, width / 2 - 100, height - 24, "Accounts"));

        synchronized (threadLock) {
            field_92023_s = fontRendererObj.getStringWidth(openGLWarning1);
            field_92024_r = fontRendererObj.getStringWidth(openGLWarning2);
            int k = Math.max(field_92023_s, field_92024_r);
            field_92022_t = (width - k) / 2;
            field_92021_u = buttonList.get(0).yPosition - 24;
            field_92020_v = field_92022_t + k;
            field_92019_w = field_92021_u + 24;
        }
    }

    /**
     * @author booky10
     */
    @Overwrite
    public void updateScreen() {
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void onActionPerformed(GuiButton button, CallbackInfo info) {
        if (button.id == Constants.ACCOUNTS_BUTTON_ID)
            mc.displayGuiScreen(new AccountSelectorGUI());
    }

    private void renderBackground() {
        ScaledResolution resolution = new ScaledResolution(mc);

        mc.getTextureManager().bindTexture(backgroundTexture);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, resolution.getScaledWidth(), resolution.getScaledHeight(), 1920, 1080);
    }

    /**
     * @author booky10
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.disableAlpha();
        renderBackground();
        GlStateManager.enableAlpha();

        int x = width / 2 - 274 / 2;
        int y = 30;

        drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);

        mc.getTextureManager().bindTexture(minecraftTitleTextures);
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

        List<String> brandings = Lists.reverse(FMLCommonHandler.instance().getBrandings(true));
        for (int line = 0; line < brandings.size(); line++) {
            String branding = brandings.get(line);
            if (!Strings.isNullOrEmpty(branding))
                drawString(fontRendererObj, branding, 2, height - (10 + line * (fontRendererObj.FONT_HEIGHT + 1)), 16777215);
        }

        String copyright = "Copyright Mojang AB. Do not distribute!";
        drawString(fontRendererObj, copyright, width - fontRendererObj.getStringWidth(copyright) - 2, height - 10, -1);

        if (openGLWarning1 != null && openGLWarning1.length() > 0) {
            drawRect(field_92022_t - 2, field_92021_u - 2, field_92020_v + 2, field_92019_w - 1, 1428160512);
            drawString(fontRendererObj, openGLWarning1, field_92022_t, field_92021_u, -1);
            drawString(fontRendererObj, openGLWarning2, (width - field_92024_r) / 2, buttonList.get(0).yPosition - 12, -1);
        }

        drawCenteredString(mc.fontRendererObj, I18n.format("accounts.logged", mc.getSession().getUsername()), width / 2, height / 4 + 169, 0xFFCC8888);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}