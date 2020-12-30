package tk.bookyclient.bookyclient.accounts.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import tk.bookyclient.bookyclient.accounts.encryption.EncryptionTools;
import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;

import java.io.IOException;

public abstract class AbstractAccountGUI extends GuiScreen {

    private final String actionString;

    private GuiTextField username, password;
    private GuiButton complete;

    protected boolean hasUserChanged = false;

    public AbstractAccountGUI(String actionString) {
        this.actionString = actionString;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.clear();
        buttonList.add(complete = new GuiButton(2, width / 2 - 152, height - 28, 150, 20, I18n.format(actionString)));
        buttonList.add(new GuiButton(3, width / 2 + 2, height - 28, 150, 20, I18n.format("gui.cancel")));

        username = new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
        username.setFocused(true);
        username.setMaxStringLength(64);

        password = new PasswordField(1, fontRendererObj, width / 2 - 100, 90, 200, 20);
        password.setMaxStringLength(64);

        complete.enabled = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, I18n.format(actionString), width / 2, 7, -1);
        drawCenteredString(fontRendererObj, I18n.format("accounts.username"), width / 2 - 130, 66, -1);
        drawCenteredString(fontRendererObj, I18n.format("accounts.password"), width / 2 - 130, 96, -1);

        username.drawTextBox();
        password.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char character, int keyIndex) {
        if (keyIndex == Keyboard.KEY_ESCAPE) escape();
        else if (keyIndex == Keyboard.KEY_RETURN) {
            if (username.isFocused()) {
                username.setFocused(false);
                password.setFocused(true);
            } else if (password.isFocused() && complete.enabled) {
                complete();
                escape();
            }
        } else if (keyIndex == Keyboard.KEY_TAB) {
            username.setFocused(!username.isFocused());
            password.setFocused(!password.isFocused());
        } else {
            username.textboxKeyTyped(character, keyIndex);
            password.textboxKeyTyped(character, keyIndex);

            if (username.isFocused()) hasUserChanged = true;
        }
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
        complete.enabled = canComplete();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) if (button.id == 2) {
            complete();
            escape();
        } else if (button.id == 3) escape();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    private void escape() {
        mc.displayGuiScreen(new AccountSelectorGUI());
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public void setPassword(String password) {
        this.password.setText(password);
    }

    protected boolean accountNotInList() {
        for (AccountData data : AccountDatabase.getInstance().getAccounts())
            if (EncryptionTools.decode(data.user).equals(getUsername()))
                return false;
        return true;
    }

    public boolean canComplete() {
        return getUsername().length() > 0 && accountNotInList();
    }

    public abstract void complete();
}
