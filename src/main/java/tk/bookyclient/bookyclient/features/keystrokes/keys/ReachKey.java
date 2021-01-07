package tk.bookyclient.bookyclient.features.keystrokes.keys;
// Created by booky10 in bookyClient (20:35 17.09.20)

import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesTracker;

public class ReachKey extends Key {

    public ReachKey(int xOffset, int yOffset) {
        super(xOffset, yOffset);
    }

    @Override
    public void renderKey(int x, int y) {
        int yOffset = this.yOffset - 18;

        if (!settings.keystrokesMouseButtons) yOffset -= 24;
        if (!settings.keystrokesSpacebar) yOffset -= 19;
        if (!settings.keystrokesSneak) yOffset -= 18;
        if (!settings.keystrokesWASD) yOffset -= 48;
        if (!settings.keystrokesFPS) yOffset -= 18;
        if (!settings.keystrokesPing) yOffset -= 18;

        int textColor = getColor();
        drawRect(x + xOffset, y + yOffset, x + xOffset + 70, y + yOffset + 16, -1912602624);

        KeystrokesTracker.tickReach();
        String text = KeystrokesTracker.getReach();
        if (settings.keystrokesChroma)
            drawChromaString(text, x + (xOffset + 70) / 2 - fontRendererObj.getStringWidth(text) / 2, y + (yOffset + 4), 1.0);
        else
            drawCenteredString(fontRendererObj, text, x + (xOffset + 70) / 2, y + (yOffset + 4), textColor);
    }
}