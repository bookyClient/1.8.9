package tk.bookyclient.bookyclient.accounts.gui.components;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class PasswordBox extends GuiTextField {

    public PasswordBox(int id, int x, int y, int width, int height) {
        super(id, Minecraft.getMinecraft().fontRendererObj, x, y, width, height);
    }

    @Override
    public void drawTextBox() {
        String password = text;
        text = Strings.repeat('*', password.length());

        super.drawTextBox();
        text = password;
    }

    @Override
    public boolean textboxKeyTyped(char character, int code) {
        return !GuiScreen.isKeyComboCtrlC(code) && !GuiScreen.isKeyComboCtrlX(code) && super.textboxKeyTyped(character, code);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        String password = text;
        text = Strings.repeat('*', getText().length());

        super.mouseClicked(x, y, button);
        text = password;
    }
}
