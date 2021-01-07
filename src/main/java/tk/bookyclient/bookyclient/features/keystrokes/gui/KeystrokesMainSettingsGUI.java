package tk.bookyclient.bookyclient.features.keystrokes.gui;
// Created by booky10 in bookyClient (23:37 18.09.20)

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;
import tk.bookyclient.bookyclient.utils.gui.ClientScreenGUI;

import java.awt.*;
import java.io.IOException;

public class KeystrokesMainSettingsGUI extends ClientScreenGUI {

    private final String screenTitle = I18n.format("client.gui.prefix") + " - " + I18n.format("keystrokes.gui.main.title");
    private GuiButton cpsCounterButton, toggleButton, doneButton;
    private boolean dragging;
    private int lastMouseX, lastMouseY;
    private final GuiScreen parentGui;

    public KeystrokesMainSettingsGUI(GuiScreen parentGui) {
        this.parentGui = parentGui;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        // Toggles
        buttonList.add(toggleButton = new GuiButton(0, width / 2 - 155, height / 6 + 18, 150, 20, getTranslatedSetting("keystrokes.gui.main.toggle", "keystrokes")));
        buttonList.add(new GuiButton(1, width / 2 - 155, height / 6 + 66, 150, 20, getTranslatedSetting("keystrokes.gui.main.mouse", "keystrokesMouseButtons")));
        buttonList.add(new GuiButton(2, width / 2 + 5, height / 6 + 66, 150, 20, getTranslatedSetting("keystrokes.gui.main.spacebar", "keystrokesSpacebar")));
        buttonList.add(cpsCounterButton = new GuiButton(3, width / 2 - 155, height / 6 + 90, 150, 20, getTranslatedSetting("keystrokes.gui.main.cps", "keystrokesCPS")));
        buttonList.add(new GuiButton(4, width / 2 + 5, height / 6 + 90, 150, 20, getTranslatedSetting("keystrokes.gui.main.sneak", "keystrokesSneak")));
        buttonList.add(new GuiButton(5, width / 2 - 155, height / 6 + 114, 150, 20, getTranslatedSetting("keystrokes.gui.main.fps", "keystrokesFPS")));
        buttonList.add(new GuiButton(6, width / 2 + 5, height / 6 + 114, 150, 20, getTranslatedSetting("keystrokes.gui.main.wasd", "keystrokesWASD")));
        buttonList.add(new GuiButton(7, width / 2 - 155, height / 6 + 138, 150, 20, getTranslatedSetting("keystrokes.gui.main.ping", "keystrokesPing")));
        buttonList.add(new GuiButton(8, width / 2 + 5, height / 6 + 138, 150, 20, getTranslatedSetting("keystrokes.gui.main.reach", "keystrokesReach")));

        // Menus
        buttonList.add(new GuiButton(11, width / 2 - 155, height / 6 + 42, 150, 20, I18n.format("keystrokes.gui.colors.name")));
        buttonList.add(new GuiButton(12, width / 2 + 5, height / 6 + 42, 150, 20, I18n.format("keystrokes.gui.custom.name")));

        // Slider
        buttonList.add(new KeystrokesScaleSliderGUI(width / 2 + 5, height / 6 + 18, 150, 20));

        // Done Button
        buttonList.add(doneButton = new GuiButton(13, width / 2 - 100, height / 6 + 168, I18n.format("gui.done")));

        // Dis/Enabling
        if (settings.keystrokes) for (GuiButton guiButton : buttonList) guiButton.enabled = true;
        else for (GuiButton guiButton : buttonList) guiButton.enabled = false;

        toggleButton.enabled = true;
        doneButton.enabled = true;
        cpsCounterButton.enabled = settings.keystrokes && settings.keystrokesMouseButtons;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                settings.keystrokes = !settings.keystrokes;
                button.displayString = getTranslatedSetting("keystrokes.gui.main.toggle", "keystrokes");

                if (settings.keystrokes) for (GuiButton guiButton : buttonList) guiButton.enabled = true;
                else for (GuiButton guiButton : buttonList) guiButton.enabled = false;

                toggleButton.enabled = true;
                doneButton.enabled = true;
                cpsCounterButton.enabled = settings.keystrokesMouseButtons && settings.keystrokes;
                break;
            case 1:
                settings.keystrokesMouseButtons = !settings.keystrokesMouseButtons;
                button.displayString = getTranslatedSetting("keystrokes.gui.main.mouse", "keystrokesMouseButtons");
                cpsCounterButton.enabled = settings.keystrokesMouseButtons;
                break;
            case 2:
                settings.keystrokesSpacebar = !settings.keystrokesSpacebar;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.spacebar", "keystrokesSpacebar"));
                break;
            case 3:
                settings.keystrokesCPS = !settings.keystrokesCPS;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.cps", "keystrokesCPS"));
                break;
            case 4:
                settings.keystrokesSneak = !settings.keystrokesSneak;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.sneak", "keystrokesSneak"));
                break;
            case 5:
                settings.keystrokesFPS = !settings.keystrokesFPS;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.fps", "keystrokesFPS"));
                break;
            case 6:
                settings.keystrokesWASD = !settings.keystrokesWASD;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.wasd", "keystrokesWASD"));
                break;
            case 7:
                settings.keystrokesPing = !settings.keystrokesPing;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.ping", "keystrokesPing"));
                break;
            case 8:
                settings.keystrokesReach = !settings.keystrokesReach;
                button.displayString = (getTranslatedSetting("keystrokes.gui.main.reach", "keystrokesReach"));
                break;
            case 11:
                mc.displayGuiScreen(new KeystrokesColorSettingsGUI(this));
                break;
            case 12:
                mc.displayGuiScreen(new KeystrokesCustomKeySettingsGUI(this));
                break;
            case 13:
                mc.displayGuiScreen(parentGui);
                break;
            default:
                super.actionPerformed(button);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (button != 0) return;

        if (!settings.keystrokes) return;
        double scale = settings.keystrokesScale;

        int x = KeystrokesUtils.getRenderX();
        int y = KeystrokesUtils.getRenderY();

        int startX = (int) ((x - 4) * scale);
        int startY = (int) ((y - 4) * scale);

        int endX = (int) (startX + KeystrokesUtils.getWidth() * scale);
        int endY = (int) (startY + KeystrokesUtils.getHeight() * scale);

        if (mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY) {
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int action) {
        super.mouseReleased(mouseX, mouseY, action);
        dragging = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (!dragging) return;
        double scale = settings.keystrokesScale;

        settings.keystrokesX = (int) (KeystrokesUtils.getRenderX() + (mouseX - lastMouseX) / scale);
        settings.keystrokesY = (int) (KeystrokesUtils.getRenderY() + (mouseY - lastMouseY) / scale);

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, screenTitle, width / 2, 15,-1);
        KeystrokesUtils.renderer.renderKeystrokes();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}