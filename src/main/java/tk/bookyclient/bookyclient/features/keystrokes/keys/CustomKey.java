package tk.bookyclient.bookyclient.features.keystrokes.keys;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.bookyclient.bookyclient.utils.gui.ClientBlockGUI;

import java.awt.*;
import java.io.Serializable;

public class CustomKey extends Key implements Serializable {

    private int key, type;
    private boolean wasPressed = false;
    private long lastPress = 0;
    private final ClientBlockGUI block = new ClientBlockGUI(0, 0, 0, 0);

    public CustomKey(int key, int type) {
        super(0, 0);

        this.key = key;
        this.type = type;
    }

    public ClientBlockGUI getBlock() {
        return block;
    }

    public void setKey(int key) {
        this.key = key;
    }

    private boolean isButtonDown(int buttonCode) {
        if (buttonCode < 0) return Mouse.isButtonDown(buttonCode + 100);
        return buttonCode > 0 && Keyboard.isKeyDown(buttonCode);
    }

    public void renderKey(int x, int y) {
        Keyboard.poll();

        boolean pressed = isButtonDown(key);
        boolean chroma = settings.keystrokesChroma;
        double fadeTime = 0.25;

        String name = (type == 0) ? (chroma ? "------" : (EnumChatFormatting.STRIKETHROUGH.toString() + "-----")) : getKeyOrMouseName(key);

        if (pressed != wasPressed) {
            wasPressed = pressed;
            lastPress = System.currentTimeMillis();
        }

        int textColor = getColor();
        int pressedColor = getPressedColor();
        int color;
        double textBrightness;

        if (pressed) {
            color = Math.min(255, (int) (fadeTime * 5.0 * (System.currentTimeMillis() - lastPress)));
            textBrightness = Math.max(0.0, 1.0 - (System.currentTimeMillis() - lastPress) / (fadeTime * 2.0));
        } else {
            color = Math.max(0, 255 - (int) (fadeTime * 5.0 * (System.currentTimeMillis() - lastPress)));
            textBrightness = Math.min(1.0, (System.currentTimeMillis() - lastPress) / (fadeTime * 2.0));
        }

        int left = x + xOffset, top = y + yOffset, right, bottom;

        if (type == 0 || type == 1) {
            right = x + xOffset + 70;
            bottom = y + yOffset + 16;
        } else {
            right = x + xOffset + 22;
            bottom = y + yOffset + 22;
        }
        Gui.drawRect(left, top, right, bottom, -1912602624 + (color << 16) + (color << 8) + color);

        block.setLeft(left);
        block.setTop(top);
        block.setRight(right);
        block.setBottom(bottom);

        int red = textColor >> 16 & 0xFF;
        int green = textColor >> 8 & 0xFF;
        int blue = textColor & 0xFF;
        int colorN = -16777216 + ((int) (red * textBrightness) << 16) + ((int) (green * textBrightness) << 8) + (int) (blue * textBrightness);
        float yPos = y + yOffset + 8;

        if (chroma) {
            if (type == 0) {
                int xIn = x + (xOffset + 76) / 4;
                int y2 = y + yOffset + 9;

                GlStateManager.pushMatrix();
                GlStateManager.translate((float) xIn, (float) y2, 0.0f);
                GlStateManager.rotate(-90.0f, 0.0f, 0.0f, 1.0f);

                drawGradientRect(0, 0, 2, 35, Color.HSBtoRGB((System.currentTimeMillis() - xIn * 10 - y2 * 10) % 2000L / 2000.0f, 0.8f, 0.8f), Color.HSBtoRGB((System.currentTimeMillis() - (xIn + 35) * 10 - y2 * 10) % 2000L / 2000.0f, 0.8f, 0.8f));
                GlStateManager.popMatrix();
            } else if (type == 1) {
                drawChromaString(name, x + (xOffset + 70) / 2 - fontRendererObj.getStringWidth(name) / 2, y + yOffset + 5, 1.0);
            } else {
                drawChromaString(name, (left + right) / 2 - fontRendererObj.getStringWidth(name) / 2, (int) yPos, 1.0);
            }
        } else if (type == 0 || type == 1) {
            drawCenteredString(fontRendererObj, name, x + (xOffset + 70) / 2, y + yOffset + 5, pressed ? pressedColor : colorN);
        } else {
            drawString(fontRendererObj, name, (left + right) / 2 - fontRendererObj.getStringWidth(name) / 2, (int) yPos, pressed ? pressedColor : colorN);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
