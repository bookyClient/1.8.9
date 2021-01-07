package tk.bookyclient.bookyclient.features.keystrokes.gui;
// Created by booky10 in bookyClient (13:06 07.01.21)

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiSlider;
import tk.bookyclient.bookyclient.settings.ClientSettings;

public class KeystrokesScaleSliderGUI extends GuiSlider {

    public static final int ID = 14;
    private static final ClientSettings settings = ClientSettings.getInstance();

    public KeystrokesScaleSliderGUI(int xPos, int yPos, int width, int height) {
        super(ID, xPos, yPos, width, height, I18n.format("keystrokes.gui.main.scale") + " ", "", 0, 200, settings.keystrokesScale * 100, false, true);

        if (getValueInt() == 100) displayString = I18n.format("keystrokes.gui.main.scale") + " Normal";
    }

    @Override
    public void updateSlider() {
        super.updateSlider();

        settings.keystrokesX = 0;
        settings.keystrokesY = 0;
        settings.keystrokesScale = (float) (getValue() / 100);

        if (getValueInt() == 100) displayString = I18n.format("keystrokes.gui.main.scale") + " Normal";
    }
}