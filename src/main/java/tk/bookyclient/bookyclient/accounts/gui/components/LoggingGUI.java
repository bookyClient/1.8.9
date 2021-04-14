package tk.bookyclient.bookyclient.accounts.gui.components;
// Created by booky10 in bookyClient (19:43 12.04.21)

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class LoggingGUI extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "§l" + I18n.format("accounts.logging"), width / 2, height / 4 + 48 + 24, 16777215);
    }
}