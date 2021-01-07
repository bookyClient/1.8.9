package tk.bookyclient.bookyclient.utils.gui;
// Created by booky10 in bookyClient (21:29 04.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import tk.bookyclient.bookyclient.settings.ClientSettings;

import java.io.Serializable;

public class ClientGUI extends Gui implements Serializable {

    protected static final ClientSettings settings = ClientSettings.getInstance();
    protected static final Minecraft mc = Minecraft.getMinecraft();

    protected final FontRenderer fontRendererObj = mc.fontRendererObj;
}