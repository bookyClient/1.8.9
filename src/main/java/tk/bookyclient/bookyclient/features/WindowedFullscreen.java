package tk.bookyclient.bookyclient.features;
// Created by booky10 in bookyClient (18:28 04.01.21)

import net.minecraft.client.Minecraft;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import tk.bookyclient.bookyclient.settings.ClientSettings;

public class WindowedFullscreen {

    public static void makeWindowed(boolean fullscreen) {
        if (!ClientSettings.getInstance().windowedFullscreen) return;

        try {
            if (fullscreen) {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Display.setFullscreen(false);
                Display.setResizable(false);

                Display.setDisplayMode(Display.getDesktopDisplayMode());
                Display.setLocation(0, 0);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Display.setResizable(true);

                Minecraft minecraft = Minecraft.getMinecraft();
                Display.setDisplayMode(new DisplayMode(minecraft.displayWidth, minecraft.displayHeight));
            }
        } catch (LWJGLException exception) {
            throw new Error("Error while processing WindowedFullscreen!", exception);
        }
    }
}