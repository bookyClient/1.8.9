package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (21:47 29.12.20)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.accounts.gui.AccountSelectorGUI;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    private final ResourceLocation backgroundTexture = new ResourceLocation("bookyClient", "splash/background.png");
    private final Minecraft minecraft = Minecraft.getMinecraft();
    private ScaledResolution resolution;

    @Inject(method = "initGui", at = @At("RETURN"))
    public void onPostInitGUI(CallbackInfo callbackInfo) {
        resolution = new ScaledResolution(minecraft);
    }

    /**
     * @author booky10
     */
    @Overwrite
    public void updateScreen() {
    }

    /**
     * @author booky10
     */
    @Overwrite
    public void renderSkybox(int mouseX, int mouseY, float partialTicks) {
        minecraft.getTextureManager().bindTexture(backgroundTexture);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, resolution.getScaledWidth(), resolution.getScaledHeight(), 1920, 1080);
    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void onActionPerformed(GuiButton button, CallbackInfo info) {
        if (button.id == Constants.ACCOUNTS_BUTTON_ID)
            minecraft.displayGuiScreen(new AccountSelectorGUI());
    }
}