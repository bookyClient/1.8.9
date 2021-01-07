package tk.bookyclient.bookyclient.features.keystrokes.keys;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.bookyclient.bookyclient.utils.gui.ClientGUI;

import java.awt.*;
import java.io.Serializable;

public abstract class Key extends ClientGUI implements Serializable {

    protected final int xOffset, yOffset;

    public Key(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    protected static Color getChromaColor(double x, double y, double offsetScale) {
        return new Color(Color.HSBtoRGB((float) ((System.currentTimeMillis() - x * 14.0D * offsetScale - y * 14.0D * offsetScale) % 4200.0F) / 4200.0F, 0.8f, 0.8f));
    }

    protected static void drawChromaString(String text, int x, int y, double offsetScale) {
        for (char character : text.toCharArray()) {
            int color = getChromaColor(x, y, offsetScale).getRGB();
            String string = String.valueOf(character);

            mc.fontRendererObj.drawStringWithShadow(string, x, y, color);
            x += mc.fontRendererObj.getStringWidth(string);
        }
    }

    protected int getColor() {
        return settings.keystrokesChroma ? Color.HSBtoRGB((System.currentTimeMillis() - xOffset * 10 - yOffset * 10) % 2000L / 2000.0f, 0.8f, 0.8f) : new Color(settings.keystrokesRed, settings.keystrokesGreen, settings.keystrokesBlue).getRGB();
    }

    protected static int getPressedColor() {
        return 0xFFFFFF;
    }

    protected static String getKeyOrMouseName(int keyCode) {
        if (keyCode >= 0) return Keyboard.getKeyName(keyCode);
        String openglName = Mouse.getButtonName(keyCode + 100);

        if (openglName == null) return "null";
        if (openglName.equals("BUTTON0")) return "LMB";
        if (openglName.equals("BUTTON1")) return "RMB";

        return openglName;

    }

    protected abstract void renderKey(int x, int y);
}
