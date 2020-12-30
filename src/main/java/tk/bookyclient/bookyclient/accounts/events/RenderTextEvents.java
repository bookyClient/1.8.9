package tk.bookyclient.bookyclient.accounts.events;
// Created by booky10 in bookyClient (21:14 29.12.20)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderTextEvents {

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiMainMenu) {
            screen.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, I18n.format("accounts.logged", Minecraft.getMinecraft().getSession().getUsername()), screen.width / 2, screen.height / 4 + 169, 0xFFCC8888);
            return;
        }
        if (screen instanceof GuiMultiplayer) {
            if (!Minecraft.getMinecraft().getSession().getToken().equals("0")) return;
            screen.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, I18n.format("accounts.offline"), screen.width / 2, 10, 16737380);
        }
    }
}