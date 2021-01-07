package tk.bookyclient.bookyclient.features.keystrokes.keys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class FPSKey extends Key {

    public FPSKey(int xOffset, int yOffset) {
        super(xOffset, yOffset);
    }

    @Override
    public void renderKey(int x, int y) {
        int yOffset = this.yOffset - 18;

        if (!settings.keystrokesSpacebar) yOffset -= 19;
        if (!settings.keystrokesSneak) yOffset -= 18;
        if (!settings.keystrokesMouseButtons) yOffset -= 24;
        if (!settings.keystrokesWASD) yOffset -= 48;

        int textColor = getColor();
        Gui.drawRect(x + xOffset, y + yOffset, x + xOffset + 70, y + yOffset + 16, -1912602624);

        String name = Minecraft.getDebugFPS() + " FPS";
        if (settings.keystrokesChroma)
            drawChromaString(name, x + (xOffset + 70) / 2 - fontRendererObj.getStringWidth(name) / 2, y + (yOffset + 4), 1.0);
        else
            drawCenteredString(fontRendererObj, name, x + (xOffset + 70) / 2, y + (yOffset + 4), textColor);
    }
}
