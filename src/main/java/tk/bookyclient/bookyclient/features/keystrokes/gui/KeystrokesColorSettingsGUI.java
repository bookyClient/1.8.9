package tk.bookyclient.bookyclient.features.keystrokes.gui;
// Created by booky10 in bookyClient (23:36 18.09.20)

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;
import tk.bookyclient.bookyclient.utils.gui.ClientColorSettingGUI;

public class KeystrokesColorSettingsGUI extends ClientColorSettingGUI {

    public KeystrokesColorSettingsGUI(GuiScreen parentGuiScreen) {
        super(parentGuiScreen, I18n.format("keystrokes.gui.colors.title"));
    }

    @Override
    public Boolean isActivated() {
        return null;
    }

    @Override
    public Boolean hasChroma() {
        return settings.keystrokesChroma;
    }

    @Override
    public Integer getRed() {
        return settings.keystrokesRed;
    }

    @Override
    public Integer getGreen() {
        return settings.keystrokesGreen;
    }

    @Override
    public Integer getBlue() {
        return settings.keystrokesBlue;
    }

    @Override
    public Integer getOpacity() {
        return null;
    }

    @Override
    public void setActivated(Boolean activated) {
    }

    @Override
    public void setChroma(Boolean chroma) {
        settings.keystrokesChroma = chroma;
    }

    @Override
    public void setRed(Integer red) {
        settings.keystrokesRed = red;
    }

    @Override
    public void setGreen(Integer green) {
        settings.keystrokesGreen = green;
    }

    @Override
    public void setBlue(Integer blue) {
        settings.keystrokesBlue = blue;
    }

    @Override
    public void setOpacity(Integer opacity) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        KeystrokesUtils.renderer.renderKeystrokes();
    }
}