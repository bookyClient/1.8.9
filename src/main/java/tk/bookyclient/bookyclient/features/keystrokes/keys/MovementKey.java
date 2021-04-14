package tk.bookyclient.bookyclient.features.keystrokes.keys;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MovementKey extends Key {

    private final KeyBinding key;
    private boolean wasPressed;
    private long lastPress = 0;

    public MovementKey(KeyBinding key, int xOffset, int yOffset) {
        super(xOffset, yOffset);
        this.key = key;
    }

    private boolean isKeyOrMouseDown(int keyCode) {
        return (keyCode < 0) ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
    }

    public void renderKey(int x, int y) {
        Keyboard.poll();
        boolean pressed = isKeyOrMouseDown(key.getKeyCode());
        double fadeTime = 0.25;
        String name = getKeyOrMouseName(key.getKeyCode());

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
            textBrightness = Math.max(0.0, 1.0 - (System.currentTimeMillis() - lastPress) / (fadeTime * 5.0));
        } else {
            color = Math.max(0, 255 - (int) (fadeTime * 5.0 * (System.currentTimeMillis() - lastPress)));
            textBrightness = Math.min(1.0, (System.currentTimeMillis() - lastPress) / (fadeTime * 5.0));
        }

        Gui.drawRect(x + xOffset, y + yOffset, x + xOffset + 22, y + yOffset + 22, -1912602624 + (color << 16) + (color << 8) + color);

        int keyWidth = 22;
        int red = textColor >> 16 & 0xFF;
        int green = textColor >> 8 & 0xFF;
        int blue = textColor & 0xFF;
        int colorN = -16777216 + ((int) (red * textBrightness) << 16) + ((int) (green * textBrightness) << 8) + (int) (blue * textBrightness);

        int stringWidth = fontRendererObj.getStringWidth(name);
        float scaleFactor = 1.0f;

        if (stringWidth > keyWidth) scaleFactor = (float) keyWidth / stringWidth;

        GlStateManager.pushMatrix();
        float xPos = x + xOffset + 8;
        float yPos = y + yOffset + 8;
        GlStateManager.scale(scaleFactor, scaleFactor, 1.0f);

        if (scaleFactor != 1.0f) {
            float scaleFactorRec = 1.0f / scaleFactor;
            xPos = (x + xOffset) * scaleFactorRec + 1.0f;
            yPos *= scaleFactorRec;
        } else if (name.length() > 1) {
            xPos -= stringWidth >> 2;
        }

        if (settings.keystrokesChroma) {
            drawChromaString(name, (int) xPos, (int) yPos, 1.0);
        } else {
            drawString(fontRendererObj, name, (int) xPos, (int) yPos, pressed ? pressedColor : colorN);
        }

        GlStateManager.popMatrix();
    }
}
