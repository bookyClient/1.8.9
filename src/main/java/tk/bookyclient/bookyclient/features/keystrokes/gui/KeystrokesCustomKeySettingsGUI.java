package tk.bookyclient.bookyclient.features.keystrokes.gui;
// Created by booky10 in bookyClient (23:37 18.09.20)

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.bookyclient.bookyclient.features.keystrokes.KeystrokesUtils;
import tk.bookyclient.bookyclient.features.keystrokes.keys.CustomKey;
import tk.bookyclient.bookyclient.features.keystrokes.render.ExtendedCustomKey;
import tk.bookyclient.bookyclient.utils.gui.ClientBlockGUI;
import tk.bookyclient.bookyclient.utils.gui.ClientScreenGUI;

import java.io.IOException;

public class KeystrokesCustomKeySettingsGUI extends ClientScreenGUI {

    private ExtendedCustomKey selected, currentlyDragging;
    private int lastMouseX, lastMouseY;
    private boolean listeningForNewKey;
    private GuiButton changeKey, changeType, delete;
    private final GuiScreen parentGui;
    private final String screenTitle = I18n.format("keystrokes.gui.custom.title");

    KeystrokesCustomKeySettingsGUI(GuiScreen parentGui) {
        this.parentGui = parentGui;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        buttonList.add(new GuiButton(1, width / 2 - 75, height / 6 + 42, 150, 20, I18n.format("keystrokes.gui.custom.add")));
        buttonList.add(changeKey = new GuiButton(2, width / 2 - 75, height / 6 + 66, 150, 20, I18n.format("keystrokes.gui.custom.change.key")));
        buttonList.add(changeType = new GuiButton(3, width / 2 - 75, height / 6 + 90, 150, 20, I18n.format("keystrokes.gui.custom.change.type")));
        buttonList.add(delete = new GuiButton(4, width / 2 - 75, height / 6 + 114, 150, 20, I18n.format("keystrokes.gui.custom.delete")));

        buttonList.add(new GuiButton(5, width / 2 - 100, height / 6 + 144, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        switch (button.id) {
            case 1:
                ExtendedCustomKey key = new ExtendedCustomKey(new CustomKey(30, 1), 10, 10);

                selected = key;
                settings.keystrokesCustomKeys.add(key);
                break;
            case 2:
                listeningForNewKey = true;
                button.displayString = I18n.format("keystrokes.gui.custom.change.listening");
                break;
            case 3:
                CustomKey theKey = selected.getKey();
                theKey.setType(theKey.getType() + 1);
                if (theKey.getType() > 2) theKey.setType(0);
                break;
            case 4:
                settings.keystrokesCustomKeys.remove(selected);
                selected = null;
                break;
            case 5:
                mc.displayGuiScreen(parentGui);
                break;
        }
    }

    @Override
    public void handleInput() throws IOException {
        if (!listeningForNewKey) {
            super.handleInput();
            return;
        }

        testForMouse:
        {
            if (!Mouse.next()) break testForMouse;
            int eventButton = Mouse.getEventButton();
            if (!Mouse.isButtonDown(eventButton)) break testForMouse;

            selected.getKey().setKey(eventButton - 100);
            listeningForNewKey = false;
            changeKey.displayString = I18n.format("keystrokes.gui.custom.change.key");
            return;
        }

        testForKeyboard:
        {
            if (!Keyboard.next()) break testForKeyboard;
            int eventKey = Keyboard.getEventKey();
            if (!Keyboard.isKeyDown(eventKey)) break testForKeyboard;

            selected.getKey().setKey(eventKey);
            listeningForNewKey = false;
            changeKey.displayString = I18n.format("keystrokes.gui.custom.change.key");
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean flag = false;
        for (GuiButton guiButton : buttonList)
            if (guiButton.isMouseOver()) {
                flag = true;
                break;
            }

        if (!flag) selected = null;

        for (ExtendedCustomKey extendedCustomKey : settings.keystrokesCustomKeys) {
            if (!extendedCustomKey.getKey().getBlock().scale(settings.keystrokesScale).isMouseOver(mouseX, mouseY)) continue;

            selected = extendedCustomKey;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (currentlyDragging == null) return;
        double scale = settings.keystrokesScale;

        currentlyDragging.setxOffset(currentlyDragging.getXOffset() + (mouseX - lastMouseX) / scale);
        currentlyDragging.setyOffset(currentlyDragging.getyOffset() + (mouseY - lastMouseY) / scale);
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int action) {
        super.mouseReleased(mouseX, mouseY, action);
        currentlyDragging = null;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        double scale = settings.keystrokesScale;

        delete.enabled = (selected != null);
        changeType.enabled = (selected != null);
        changeKey.enabled = (selected != null);

        if (selected != null) {
            ClientBlockGUI block = selected.getKey().getBlock().scale(scale);
            drawRect(block.getLeft(), block.getTop(), block.getRight(), block.getBottom(), -1);
        }

        KeystrokesUtils.renderer.renderKeystrokes();
        drawCenteredString(fontRendererObj, screenTitle, width / 2, 15, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!(Mouse.isButtonDown(0) && selected != null && currentlyDragging == null)) return;
        if (!selected.getKey().getBlock().scale(scale).isMouseOver(mouseX, mouseY)) return;

        currentlyDragging = selected;
    }
}