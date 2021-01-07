package tk.bookyclient.bookyclient.utils.gui;
// Created by booky10 in bookyClient (20:32 04.01.21)

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.bookyclient.bookyclient.settings.ClientSettings;

import java.lang.reflect.Field;

public class ClientScreenGUI extends GuiScreen {

    protected static final ClientSettings settings = ClientSettings.getInstance();

    protected String getTranslatedSetting(String key, String setting) {
        try {
            Field field = ClientSettings.class.getField(setting);
            return I18n.format(key, (boolean) field.get(settings) ? I18n.format("options.on") : I18n.format("options.off"));
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }
}