package tk.bookyclient.bookyclient.features.keystrokes;
// Created by booky10 in bookyClient (19:05 17.09.20)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.bookyclient.bookyclient.features.keystrokes.render.KeystrokesRenderer;
import tk.bookyclient.bookyclient.settings.ClientSettings;

public class KeystrokesUtils {

    public static final KeystrokesRenderer renderer = new KeystrokesRenderer();
    private static final ClientSettings settings = ClientSettings.getInstance();

    public static int getHeight() {
        int height = 32;

        if (settings.keystrokesCPS || settings.keystrokesSpacebar || settings.keystrokesFPS) height += 24;
        if (settings.keystrokesMouseButtons) height += 24;
        if (settings.keystrokesWASD) height += 48;
        if (!settings.keystrokesFPS) height -= 18;
        if (!settings.keystrokesSneak) height -= 18;
        if (!settings.keystrokesFPS) height -= 18;
        if (!settings.keystrokesPing) height -= 18;

        return height;
    }

    public static int getWidth() {
        return 74;
    }

    private static double capDouble(double valueIn, double minValue, double maxValue) {
        return (valueIn < minValue) ? minValue : Math.min(valueIn, maxValue);
    }

    public static int getRenderX() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        int width = getWidth(), x = settings.keystrokesX;
        double scale = settings.keystrokesScale;

        if (x < 0)
            x = 0;
        else if (x * scale > resolution.getScaledWidth() - width * scale)
            x = (int) ((resolution.getScaledWidth() - width * scale) / scale);

        return x;
    }

    public static int getRenderY() {
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        int height = getHeight(), y = settings.keystrokesY;
        double scale = settings.keystrokesScale;

        if (y < 0)
            y = 0;
        else if (y * scale > resolution.getScaledHeight() - height * scale)
            y = (int) ((resolution.getScaledHeight() - height * scale) / scale);

        return y;
    }
}