package tk.bookyclient.bookyclient.accounts.gui;

import joptsimple.internal.Strings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class PasswordField extends GuiTextField {

    public PasswordField(Integer componentId, FontRenderer fontRenderer, Integer x, Integer y,
                         Integer par5Width, Integer par6Height) {
        super(componentId, fontRenderer, x, y, par5Width, par6Height);
    }

    @Override
    public void drawTextBox() {
        String password = getText();

        replaceText(Strings.repeat('*', getText().length()));
        super.drawTextBox();
        replaceText(password);
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        return !GuiScreen.isKeyComboCtrlC(keyCode)
                && !GuiScreen.isKeyComboCtrlX(keyCode)
                && super.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        String password = getText();

        replaceText(Strings.repeat('*', getText().length()));
        super.mouseClicked(mouseX, mouseY, mouseButton);
        replaceText(password);
    }

    private void replaceText(String newText) {
        int cursorPosition = getCursorPosition();
        int selectionEnd = getSelectionEnd();

        setText(newText);
        setCursorPosition(cursorPosition);
        setSelectionPos(selectionEnd);
    }
}
