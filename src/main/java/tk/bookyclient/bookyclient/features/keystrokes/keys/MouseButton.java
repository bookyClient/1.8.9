package tk.bookyclient.bookyclient.features.keystrokes.keys;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesTracker;

public class MouseButton extends Key {

    private static final String[] BUTTONS = new String[]{"LMB", "RMB"};

    private final int button;
    private boolean wasPressed;
    private long lastPress = 0;

    public MouseButton(int button, int xOffset, int yOffset) {
        super(xOffset, yOffset);
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public void renderKey(int x, int y) {
        boolean pressed = Mouse.isButtonDown(button);
        int yOffset = this.yOffset;
        Mouse.poll();

        if (!settings.keystrokesWASD) yOffset -= 48;

        String name = MouseButton.BUTTONS[button];
        double fadeTime = 0.25;

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

        Gui.drawRect(x + xOffset, y + yOffset, x + xOffset + 34, y + yOffset + 22, -1912602624 + (color << 16) + (color << 8) + color);
        int red = textColor >> 16 & 0xFF, green = textColor >> 8 & 0xFF, blue = textColor & 0xFF;
        int colorN = -16777216 + ((int) (red * textBrightness) << 16) + ((int) (green * textBrightness) << 8) + (int) (blue * textBrightness);

        if (settings.keystrokesCPS) {
            KeystrokesTracker.tickCPS();

            int roundedX = Math.round(x / 0.5f + xOffset / 0.5f + 20.0f);
            int roundedY = Math.round(y / 0.5f + yOffset / 0.5f + 28.0f);

            if (settings.keystrokesChroma) {
                drawChromaString(name, x + xOffset + 8, y + yOffset + 4, 1.0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5f, 0.5f, 0.0f);
                drawChromaString((name.equals(MouseButton.BUTTONS[0]) ? KeystrokesTracker.getLeftCPS() : KeystrokesTracker.getRightCPS()) + " CPS", roundedX, roundedY, 0.5);
            } else {
                drawString(fontRendererObj, name, x + xOffset + 8, y + yOffset + 4, pressed ? pressedColor : colorN);
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5f, 0.5f, 0.0f);
                drawString(fontRendererObj, (name.equals(MouseButton.BUTTONS[0]) ? KeystrokesTracker.getLeftCPS() : KeystrokesTracker.getRightCPS()) + " CPS", roundedX, roundedY, pressed ? pressedColor : colorN);
            }

            GlStateManager.popMatrix();
        } else if (settings.keystrokesChroma) {
            drawChromaString(name, x + xOffset + 8, y + yOffset + 8, 1.0);
        } else {
            drawString(fontRendererObj, name, x + xOffset + 8, y + yOffset + 8, pressed ? pressedColor : colorN);
        }
    }
}
