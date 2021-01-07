package tk.bookyclient.bookyclient.features.keystrokes.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import tk.bookyclient.bookyclient.features.keystrokes.keys.*;
import tk.bookyclient.bookyclient.settings.ClientSettings;

public class KeystrokesRenderer {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ClientSettings settings = ClientSettings.getInstance();

    private final FPSKey fpsKey;
    private final PingKey pingKey;
    private final ReachKey reachKey;
    private final SpaceKey spaceKey, sneakKey;

    private final MovementKey[] movementKeys = new MovementKey[4];
    private final MouseButton[] mouseButtons = new MouseButton[2];

    public KeystrokesRenderer() {
        sneakKey = new SpaceKey(mc.gameSettings.keyBindSneak, 2, 74, "Sneak");
        fpsKey = new FPSKey(2, 128);
        pingKey = new PingKey(2, 146);
        spaceKey = new SpaceKey(mc.gameSettings.keyBindJump, 2, 92, "Space");
        reachKey = new ReachKey(2, 164);

        movementKeys[0] = new MovementKey(mc.gameSettings.keyBindForward, 26, 2);
        movementKeys[1] = new MovementKey(mc.gameSettings.keyBindBack, 26, 26);
        movementKeys[2] = new MovementKey(mc.gameSettings.keyBindLeft, 2, 26);
        movementKeys[3] = new MovementKey(mc.gameSettings.keyBindRight, 50, 26);

        mouseButtons[0] = new MouseButton(0, 2, 50);
        mouseButtons[1] = new MouseButton(1, 38, 50);
    }

    public void renderKeystrokes() {
        if (!settings.keystrokes) return;

        int x = settings.keystrokesX, y = settings.keystrokesY;
        double scale = settings.keystrokesScale;

        boolean showingMouseButtons = settings.keystrokesMouseButtons;
        boolean showingSpacebar = settings.keystrokesSpacebar;
        boolean showingSneak = settings.keystrokesSneak;
        boolean showingFPS = settings.keystrokesFPS;
        boolean showingPing = settings.keystrokesPing;
        boolean showingWASD = settings.keystrokesWASD;
        boolean showingReach = settings.keystrokesReach;

        if (scale != 1.0) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1.0);
        }

        if (showingMouseButtons) drawMouseButtons(x, y);
        if (showingSneak) drawSneak(x, y);
        if (showingSpacebar) drawSpacebar(x, y);
        if (showingFPS) drawFPS(x, y);
        if (showingPing) drawPing(x, y);
        if (showingWASD) drawMovementKeys(x, y);
        if (showingReach) drawReachKey(x, y);

        y += 166;
        if (!showingMouseButtons) y -= 24;
        if (!showingSneak) y -= 18;
        if (!showingSpacebar) y -= 18;
        if (!showingFPS) y -= 18;
        if (!showingPing) y -= 18;
        if (!showingWASD) y -= 48;
        if (!showingReach) y -= 18;

        for (ExtendedCustomKey customKey : settings.keystrokesCustomKeys) {
            int xOffset = (int) customKey.getXOffset();
            int yOffset = (int) customKey.getyOffset();
            customKey.getKey().renderKey(x + xOffset, y + yOffset);
        }

        if (scale != 1.0) GlStateManager.popMatrix();
    }

    public void tryRender() {
        if (mc.inGameHasFocus && !mc.gameSettings.showDebugInfo) renderKeystrokes();
    }

    public MouseButton[] getMouseButtons() {
        return mouseButtons;
    }

    private void drawSneak(int x, int y) {
        sneakKey.renderKey(x, y);
    }

    private void drawFPS(int x, int y) {
        fpsKey.renderKey(x, y);
    }

    private void drawPing(int x, int y) {
        pingKey.renderKey(x, y);
    }

    private void drawSpacebar(int x, int y) {
        spaceKey.renderKey(x, y);
    }

    private void drawReachKey(int x, int y) {
        reachKey.renderKey(x, y);
    }

    private void drawMovementKeys(int x, int y) {
        for (MovementKey key : movementKeys) key.renderKey(x, y);
    }

    private void drawMouseButtons(int x, int y) {
        for (MouseButton button : mouseButtons) button.renderKey(x, y);
    }
}
