package tk.bookyclient.bookyclient.events;
// Created by booky10 in bookyClient (13:15 30.12.20)

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.bookyclient.bookyclient.utils.Constants;

public class MainMenuInitListener {

    @SubscribeEvent
    public void onGUIPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.gui instanceof GuiMainMenu)) return;
        int height = event.gui.height / 4 + 48;

        System.out.println("Injecting bookyClient Buttons...");

        event.buttonList.clear();
        event.buttonList.add(new GuiButton(1, event.gui.width / 2 - 100, height, I18n.format("menu.singleplayer")));
        event.buttonList.add(new GuiButton(2, event.gui.width / 2 - 100, height + 24, I18n.format("menu.multiplayer")));
        event.buttonList.add(new GuiButton(0, event.gui.width / 2 - 100, height + 48, I18n.format("menu.options")));
        event.buttonList.add(new GuiButton(6, event.gui.width / 2 - 100, height + 72, I18n.format("fml.menu.mods")));
        event.buttonList.add(new GuiButton(4, event.gui.width / 2 - 100, height + 96, I18n.format("menu.quit")));

        event.buttonList.add(new GuiButton(Constants.ACCOUNTS_BUTTON_ID, event.gui.width / 2 - 100, height - 24, I18n.format("accounts.accounts")));
    }
}