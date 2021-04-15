package tk.bookyclient.bookyclient.features;
// Created by booky10 in bookyClient (21:29 14.04.21)

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import tk.bookyclient.bookyclient.settings.ClientSettings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class CrashScreenGUI extends GuiScreen {

    private final String[] trace;
    private final int status;

    public CrashScreenGUI(CrashReport report, int status) {
        this.status = status;

        String[] trace = new String[0];
        try {
            if (!ClientSettings.getInstance().crashScreen) {
                throw new IOException("Crashing the game because the crash screen option is not enabled");
            }

            StringWriter string = new StringWriter();
            PrintWriter printer = new PrintWriter(string);

            report.getCrashCause().printStackTrace(printer);
            trace = string.toString().replace("\t", "    ").split("\n");

            printer.close();
            string.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            shutdown();
        }

        this.trace = trace;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        buttonList.add(new GuiOptionButton(0, width / 2 - 155, height / 4 + 120 + 120, I18n.format("gui.toTitle")));
        buttonList.add(new GuiOptionButton(1, width / 2 - 155 + 160, height / 4 + 120 + 120, I18n.format("menu.quit")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(new GuiMainMenu());
        } else if (button.id == 1) {
            shutdown();
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, "Game crashed!", width / 2, height / 4 - 40, 16777215);
        for (int i = 0; i < trace.length; i++) {
            drawString(fontRendererObj, trace[i], width / 2 - 300, height / 4 + i * 9, 10526880);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void shutdown() {
        try {
            // hacky workaround for the forge security manager
            Class<?> shutdown = Class.forName("java.lang.Shutdown");
            Method exit = shutdown.getDeclaredMethod("exit", int.class);
            exit.setAccessible(true);
            exit.invoke(null, status);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}