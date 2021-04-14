package tk.bookyclient.bookyclient.accounts.gui.main;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import tk.bookyclient.bookyclient.accounts.Account;
import tk.bookyclient.bookyclient.accounts.AccountDatabase;
import tk.bookyclient.bookyclient.accounts.Accounts;
import tk.bookyclient.bookyclient.accounts.gui.components.PasswordBox;

import java.io.IOException;

public abstract class AbstractAccountGUI extends GuiScreen {

    private final String title;
    protected boolean hasChanged;
    private GuiTextField username, password;
    private GuiButton done;

    public AbstractAccountGUI(String title) {
        this.title = title;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.clear();
        buttonList.add(done = new GuiButton(2, width / 2 - 152, height - 28, 150, 20, title));
        buttonList.add(new GuiButton(3, width / 2 + 2, height - 28, 150, 20, I18n.format("gui.cancel")));

        username = new GuiTextField(0, fontRendererObj, width / 2 - 100, 60, 200, 20);
        username.setFocused(true);
        username.setMaxStringLength(64);

        password = new PasswordBox(1, width / 2 - 100, 90, 200, 20);
        password.setMaxStringLength(64);

        done.enabled = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, title, width / 2, 7, -1);
        drawCenteredString(fontRendererObj, I18n.format("accounts.username"), width / 2 - 130, 66, -1);
        drawCenteredString(fontRendererObj, I18n.format("accounts.password"), width / 2 - 130, 96, -1);

        username.drawTextBox();
        password.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char character, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(new AccountSelectorGUI());
                break;
            case Keyboard.KEY_RETURN:
                if (!done.enabled) break;
                complete();
                mc.displayGuiScreen(new AccountSelectorGUI());
                break;
            case Keyboard.KEY_TAB:
                if (!username.isFocused()) break;
                username.setFocused(false);
                password.setFocused(true);
                break;
            default:
                if (username.isFocused()) {
                    username.textboxKeyTyped(character, key);
                    hasChanged = true;
                } else if (password.isFocused()) {
                    password.textboxKeyTyped(character, key);
                }
                break;
        }
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
        done.enabled = canComplete();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;
        if (button.id == 2) complete();
        if (button.id != 2 && button.id != 3) return;
        mc.displayGuiScreen(new AccountSelectorGUI());
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
        Accounts.save();
    }

    public String getUsername() {
        return username.getText();
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public String getPassword() {
        return password.getText();
    }

    public void setPassword(String password) {
        this.password.setText(password);
    }

    private boolean wouldDuplicate() {
        for (Account data : AccountDatabase.getAccounts()) {
            if (!Accounts.decode(data.getUsername()).equals(getUsername())) continue;
            return true;
        }
        return false;
    }

    public boolean canComplete() {
        return getUsername().length() > 0 && !wouldDuplicate();
    }

    public abstract void complete();
}
