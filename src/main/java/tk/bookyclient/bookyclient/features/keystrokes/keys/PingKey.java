package tk.bookyclient.bookyclient.features.keystrokes.keys;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PingKey extends Key {

    public PingKey(int xOffset, int yOffset) {
        super(xOffset, yOffset);
    }

    public void renderKey(int x, int y) {
        int yOffset = this.yOffset - 18;

        if (!settings.keystrokesMouseButtons) yOffset -= 24;
        if (!settings.keystrokesSpacebar) yOffset -= 19;
        if (!settings.keystrokesSneak) yOffset -= 18;
        if (!settings.keystrokesWASD) yOffset -= 48;
        if (!settings.keystrokesFPS) yOffset -= 18;

        int textColor = getColor();
        drawRect(x + xOffset, y + yOffset, x + xOffset + 70, y + yOffset + 16, -1912602624);

        NetHandlerPlayClient netHandler = mc.getNetHandler();
        String ping = null;
        if (netHandler != null) {
            NetworkPlayerInfo playerInfo = netHandler.getPlayerInfo(mc.thePlayer.getUniqueID());
            if (playerInfo != null) ping = Integer.toString(playerInfo.getResponseTime());
        }

        String text = (ping == null) ? "-" : (ping + "ms");
        if (settings.keystrokesChroma)
            drawChromaString(text, x + (xOffset + 70) / 2 - fontRendererObj.getStringWidth(text) / 2, y + (yOffset + 4), 1.0);
        else
            drawCenteredString(fontRendererObj, text, x + (xOffset + 70) / 2, y + (yOffset + 4), textColor);
    }
}
