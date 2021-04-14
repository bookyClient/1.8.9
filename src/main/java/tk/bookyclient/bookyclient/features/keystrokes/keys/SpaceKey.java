package tk.bookyclient.bookyclient.features.keystrokes.keys;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class SpaceKey extends Key {

    private final KeyBinding key;
    private boolean wasPressed = false;
    private long lastPress = 0;
    private final String name;

    public SpaceKey(KeyBinding key, int xOffset, int yOffset, String name) {
        super(xOffset, yOffset);

        this.key = key;
        this.name = name;
    }

    private boolean isButtonDown(int buttonCode) {
        if (buttonCode < 0) return Mouse.isButtonDown(buttonCode + 100);
        return buttonCode > 0 && Keyboard.isKeyDown(buttonCode);
    }

    public void renderKey(int x, int y) {
        int yOffset = this.yOffset;

        if (!settings.keystrokesMouseButtons) yOffset -= 24;
        if (!settings.keystrokesSneak) yOffset -= 18;
        if (!settings.keystrokesWASD) yOffset -= 48;

        Keyboard.poll();
        boolean pressed = isButtonDown(key.getKeyCode());
        boolean chroma = settings.keystrokesChroma;
        double fadeTime = 0.25;

        String name = this.name.equalsIgnoreCase("space") ? (chroma ? "------" : (EnumChatFormatting.STRIKETHROUGH.toString() + "-----")) : "Sneak";
        if (pressed != wasPressed) {
            wasPressed = pressed;
            lastPress = System.currentTimeMillis();
        }

        int textColor = getColor();
        int pressedColor = getPressedColor();
        double textBrightness;
        int color;

        if (pressed) {
            color = Math.min(255, (int) (fadeTime * 5.0 * (System.currentTimeMillis() - lastPress)));
            textBrightness = Math.max(0.0, 1.0 - (System.currentTimeMillis() - lastPress) / (fadeTime * 2.0));
        } else {
            color = Math.max(0, 255 - (int) (fadeTime * 5.0 * (System.currentTimeMillis() - lastPress)));
            textBrightness = Math.min(1.0, (System.currentTimeMillis() - lastPress) / (fadeTime * 2.0));
        }

        Gui.drawRect(x + xOffset, y + yOffset, x + xOffset + 70, y + yOffset + 16, -1912602624 + (color << 16) + (color << 8) + color);

        int red = textColor >> 16 & 0xFF;
        int green = textColor >> 8 & 0xFF;
        int blue = textColor & 0xFF;
        int colorN = -16777216 + ((int) (red * textBrightness) << 16) + ((int) (green * textBrightness) << 8) + (int) (blue * textBrightness);

        if (chroma) {
            if (this.name.equalsIgnoreCase("space")) {
                int xIn = x + (xOffset + 76) / 4;
                int y2 = y + yOffset + 9;

                GlStateManager.pushMatrix();
                GlStateManager.translate((float) xIn, (float) y2, 0.0f);
                GlStateManager.rotate(-90.0f, 0.0f, 0.0f, 1.0f);

                drawGradientRect(0, 0, 2, 35, Color.HSBtoRGB((System.currentTimeMillis() - xIn * 10 - y2 * 10) % 2000L / 2000.0f, 0.8f, 0.8f), Color.HSBtoRGB((System.currentTimeMillis() - (xIn + 35) * 10 - y2 * 10) % 2000L / 2000.0f, 0.8f, 0.8f));
                GlStateManager.popMatrix();
            } else {
                drawChromaString(name, x + (xOffset + 70) / 2 - fontRendererObj.getStringWidth(name) / 2, y + yOffset + 5, 1.0);
            }
        } else {
            drawCenteredString(fontRendererObj, name, x + (xOffset + 70) / 2, y + yOffset + 5, pressed ? pressedColor : colorN);
        }
    }
}
