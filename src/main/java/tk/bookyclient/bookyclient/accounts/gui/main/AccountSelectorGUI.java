package tk.bookyclient.bookyclient.accounts.gui.main;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import tk.bookyclient.bookyclient.accounts.Account;
import tk.bookyclient.bookyclient.accounts.AccountDatabase;
import tk.bookyclient.bookyclient.accounts.Accounts;
import tk.bookyclient.bookyclient.accounts.gui.components.AddAccountGUI;
import tk.bookyclient.bookyclient.accounts.gui.components.EditAccountGUI;
import tk.bookyclient.bookyclient.accounts.gui.components.LoggingGUI;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class AccountSelectorGUI extends GuiScreen {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(I18n.format("client.date"));
    private int selected, previous;
    private boolean failed;
    private AccountList accountList;
    private GuiButton login, offlineLogin, delete, edit;

    @Override
    public void initGui() {
        accountList = new AccountList(width, height);
        accountList.registerScrollButtons(5, 6);
        buttonList.clear();

        buttonList.add(login = new GuiButton(1, width / 2 - 154, height - 52, 100, 20, I18n.format("accounts.login")));
        buttonList.add(offlineLogin = new GuiButton(2, width / 2 - 154, height - 28, 100, 20, I18n.format("accounts.offlinelogin")));
        buttonList.add(edit = new GuiButton(7, width / 2 - 44, height - 52, 100, 20, I18n.format("accounts.edit")));
        buttonList.add(delete = new GuiButton(4, width / 2 - 44, height - 28, 100, 20, I18n.format("accounts.delete")));
        buttonList.add(new GuiButton(0, width / 2 + 64, height - 52, 100, 20, I18n.format("accounts.addaccount")));
        buttonList.add(new GuiButton(3, width / 2 + 64, height - 28, 100, 20, I18n.format("gui.cancel")));

        updateButtons();
        SkinUtils.cacheSkins();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        accountList.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        updateButtons();

        if (previous == selected) return;
        updateShownSkin();
        previous = selected;
    }

    private void updateShownSkin() {
        if (AccountDatabase.getAccounts().isEmpty()) return;
        SkinUtils.buildSkin(AccountDatabase.getAccounts().get(selected).getName());
    }

    @Override
    public void onGuiClosed() {
        Accounts.save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        accountList.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, I18n.format("accounts.selectaccount"), width / 2, 4, -1);

        if (failed) drawCenteredString(fontRendererObj, I18n.format("accounts.failed"), width / 2, height - 62, 16737380);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (AccountDatabase.getAccounts().isEmpty()) return;
        Account account = AccountDatabase.getAccounts().get(selected);

        SkinUtils.draw(8, height / 2 - 64 - 16, 64, 128);
        drawBorderedRect(width - 8 - 128, height / 2 - 64 - 16, width - 8, height / 2 + 64 - 16, 2, -5855578, -13421773);

        drawString(fontRendererObj, "§l§n" + I18n.format("accounts." + (account.isPremium() ? "" : "not") + "premium"), width - 8 - 125, height / 2 - 64 - 13, account.isPremium() ? 6618980 : 16737380);
        drawString(fontRendererObj, I18n.format("accounts.timesused", account.getUseCount()), width - 8 - 125, height / 2 - 64 - 15 + 21, -1);

        if (account.getLastUsed() <= 0) return;
        drawString(fontRendererObj, I18n.format("accounts.lastused"), width - 8 - 125, height / 2 - 64 - 15 + 30, -1);
        drawString(fontRendererObj, "  " + dateFormat.format(account.getLastUsed()), width - 8 - 125, height / 2 - 64 - 15 + 39, -1);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        if (button.id == 3) {
            mc.displayGuiScreen(null);
        } else if (button.id == 0) {
            mc.displayGuiScreen(new AddAccountGUI());
        } else if (button.id == 4) {
            deleteAccount();
        } else if (button.id == 1) {
            login();
        } else if (button.id == 2) {
            offlineLogin();
        } else if (button.id == 7) {
            mc.displayGuiScreen(new EditAccountGUI(selected));
        } else {
            accountList.actionPerformed(button);
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        switch (key) {
            case Keyboard.KEY_UP:
                if (AccountDatabase.getAccounts().isEmpty() || selected <= 0) break;
                --selected;
                break;
            case Keyboard.KEY_DOWN:
                if (AccountDatabase.getAccounts().isEmpty() || selected >= AccountDatabase.getAccounts().size() - 1) break;
                ++selected;
                break;
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(null);
                break;
            case Keyboard.KEY_RETURN:
                if (login.enabled) {
                    login();
                } else if (offlineLogin.enabled) {
                    offlineLogin();
                }
                break;
            case Keyboard.KEY_DELETE:
                if (!delete.enabled) break;
                deleteAccount();
                break;
            case Keyboard.KEY_N:
                if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) break;
                mc.displayGuiScreen(new AddAccountGUI());
                break;
            case Keyboard.KEY_E:
                if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) break;
                mc.displayGuiScreen(new EditAccountGUI(selected));
                break;
            case Keyboard.KEY_R:
                if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) break;
            case Keyboard.KEY_F5:
                reloadSkins();
                break;
            default:
                break;
        }
    }

    private void reloadSkins() {
        SkinUtils.cacheSkins();
        updateShownSkin();
    }

    private void deleteAccount() {
        AccountDatabase.getAccounts().remove(selected);

        if (selected > 0) selected--;
        updateButtons();
    }

    private void offlineLogin() {
        Account account = AccountDatabase.getAccounts().get(selected);
        failed = account.login();

        account.setUseCount(account.getUseCount() + 1);
        account.setLastUsed(System.currentTimeMillis());

        AccountDatabase.getAccounts().set(selected, account);
        mc.displayGuiScreen(null);
    }

    public void login() {
        mc.displayGuiScreen(new LoggingGUI());

        new Thread(() -> {
            Account account = AccountDatabase.getAccounts().get(selected);
            failed = account.login();

            if (!failed) {
                account.setUseCount(account.getUseCount() + 1);
                account.setLastUsed(System.currentTimeMillis());

                AccountDatabase.getAccounts().set(selected, account);
                mc.addScheduledTask(() -> mc.displayGuiScreen(null));
            } else {
                mc.addScheduledTask(() -> mc.displayGuiScreen(this));
            }
        }, "Authentication Thread").start();
    }

    private void updateButtons() {
        login.enabled = !AccountDatabase.getAccounts().isEmpty() && !Accounts.decode(AccountDatabase.getAccounts().get(selected).getPassword()).isEmpty();
        offlineLogin.enabled = !AccountDatabase.getAccounts().isEmpty();
        delete.enabled = !AccountDatabase.getAccounts().isEmpty();
        edit.enabled = !AccountDatabase.getAccounts().isEmpty();
    }

    private void drawBorderedRect(int x, int y, int x1, int y1, int size, int border, int color) {
        Gui.drawRect(x + size, y + size, x1 - size, y1 - size, color);
        Gui.drawRect(x + size, y + size, x1, y, border);

        Gui.drawRect(x, y, x + size, y1, border);
        Gui.drawRect(x1, y1, x1 - size, y + size, border);
        Gui.drawRect(x, y1 - size, x1, y1, border);
    }

    public class AccountList extends GuiSlot {

        public AccountList(int width, int height) {
            super(Minecraft.getMinecraft(), width, height, 32, height - 64, 14);
        }

        @Override
        protected int getSize() {
            return AccountDatabase.getAccounts().size();
        }

        @Override
        protected void elementClicked(int slot, boolean isDoubleClick, int mouseX, int mouseY) {
            selected = slot;
            updateButtons();

            if (!isDoubleClick || !login.enabled) return;
            login();
        }

        @Override
        protected boolean isSelected(int slot) {
            return slot == selected;
        }

        @Override
        protected int getContentHeight() {
            return AccountDatabase.getAccounts().size() * 14;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int entryID, int x, int y, int height, int mouseX, int mouseY) {
            Account account = AccountDatabase.getAccounts().get(entryID);
            String name = StringUtils.isEmpty(account.getName()) ? I18n.format("accounts.account", entryID + 1) : account.getName();

            if (name.contains("@")) {
                String[] split = name.split("@");
                name = String.format("%s@%s", Strings.repeat('*', split[0].length()), Strings.repeat('*', split[1].length()));
            }

            drawString(fontRendererObj, name, x + 2, y + 1, mc.getSession().getUsername().equals(account.getName()) ? 0x00FF00 : 0xFFFFFF);
        }
    }
}
