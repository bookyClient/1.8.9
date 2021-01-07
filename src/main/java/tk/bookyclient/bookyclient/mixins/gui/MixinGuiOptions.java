package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (14:30 07.01.21)

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.features.keystrokes.gui.KeystrokesMainSettingsGUI;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(GuiOptions.class)
public class MixinGuiOptions extends GuiScreen implements GuiYesNoCallback {

    @Inject(method = "initGui", at = @At("RETURN"))
    public void onInitGui(CallbackInfo callbackInfo) {
        for (GuiButton button : buttonList) {
            if (button.id == 107) {
                button.id = Constants.SETTINGS_BUTTON_ID;
                button.yPosition = height / 6 + 42;
                button.displayString = I18n.format("client.gui.name");
            } else if (button.id == 8675309) {
                button.yPosition = height / 6 + 66;
            }
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    public void onActionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if (button.id == Constants.SETTINGS_BUTTON_ID)
            mc.displayGuiScreen(new KeystrokesMainSettingsGUI(this));
    }
}