package tk.bookyclient.bookyclient.accounts.gui;

import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import tk.bookyclient.bookyclient.accounts.encryption.EncryptionTools;
import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.accounts.model.ExtendedAccountData;
import tk.bookyclient.bookyclient.accounts.skins.SkinUtils;
import tk.bookyclient.bookyclient.accounts.utils.AccountConfig;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;
import tk.bookyclient.bookyclient.accounts.utils.AccountManager;
import tk.bookyclient.bookyclient.utils.HTTPTools;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountSelectorGUI extends GuiScreen {

    private Integer selectedAccountIndex = 0, previousIndex = 0;
    private Throwable failedLogin;
    private ArrayList<ExtendedAccountData> accounts = convertData();
    private AccountList accountGUI;

    private GuiButton login, offlineLogin, delete, edit, add, cancel;

    private String query;
    private GuiTextField search;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        accountGUI = new AccountList(mc);
        accountGUI.registerScrollButtons(5, 6);
        query = I18n.format("accounts.search");

        buttonList.clear();

        buttonList.add(login = new GuiButton(1, width / 2 - 154, height - 52, 100, 20, I18n.format("accounts.login")));
        buttonList.add(offlineLogin = new GuiButton(2, width / 2 - 154, height - 28, 100, 20, I18n.format("accounts.offlinelogin")));

        buttonList.add(edit = new GuiButton(7, width / 2 - 44, height - 52, 100, 20, I18n.format("accounts.edit")));
        buttonList.add(delete = new GuiButton(4, width / 2 - 44, height - 28, 100, 20, I18n.format("accounts.delete")));

        buttonList.add(add = new GuiButton(0, width / 2 + 64, height - 52, 100, 20, I18n.format("accounts.addaccount")));
        buttonList.add(cancel = new GuiButton(3, width / 2 + 64, height - 28, 100, 20, I18n.format("gui.cancel")));

        search = new GuiTextField(8, fontRendererObj, width / 2 - 80, 14, 160, 16);
        search.setText(query);
        search.setTextColor(new Color(0xA5A5A5).getRGB());

        updateButtons();
        if (!accounts.isEmpty()) SkinUtils.buildSkin(accounts.get(selectedAccountIndex).alias);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        accountGUI.handleMouseInput();
    }

    @Override
    public void updateScreen() {
        search.updateCursorCounter();

        updateText();
        updateButtons();

        if (!(previousIndex.equals(selectedAccountIndex))) {
            updateShownSkin();
            previousIndex = selectedAccountIndex;
        }
    }

    private void updateShownSkin() {
        if (!accounts.isEmpty()) SkinUtils.buildSkin(accounts.get(selectedAccountIndex).alias);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean flag = search.isFocused();
        search.mouseClicked(mouseX, mouseY, mouseButton);

        if (!flag && search.isFocused()) {
            query = "";

            updateText();
            updateQueried();
        }
    }

    private void updateText() {
        search.setText(query);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        AccountConfig.saveToFile();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        accountGUI.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, I18n.format("accounts.selectaccount"), width / 2, 4, -1);

        if (failedLogin != null)
            drawCenteredString(fontRendererObj, failedLogin.getLocalizedMessage(), width / 2, height - 62, 16737380);
        search.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!accounts.isEmpty()) {
            SkinUtils.drawSkin(8, height / 2 - 64 - 16, 64, 128);
            drawBorderedRect(width - 8 - 128, height / 2 - 64 - 16, width - 8, height / 2 + 64 - 16, 2, -5855578, -13421773);

            if (accounts.get(selectedAccountIndex).premium)
                drawString(fontRendererObj, "§l§n" + I18n.format("accounts.premium"), width - 8 - 125, height / 2 - 64 - 13, 6618980);
            else
                drawString(fontRendererObj, "§l§n" + I18n.format("accounts.notpremium"), width - 8 - 125, height / 2 - 64 - 13, 16737380);

            drawString(fontRendererObj, I18n.format("accounts.timesused", accounts.get(selectedAccountIndex).useCount), width - 8 - 125, height / 2 - 64 - 15 + 21, -1);

            if (accounts.get(selectedAccountIndex).useCount > 0) {
                drawString(fontRendererObj, I18n.format("accounts.lastused"), width - 8 - 125, height / 2 - 64 - 15 + 30, -1);
                drawString(fontRendererObj, "  " + getFormattedDate(), width - 8 - 125, height / 2 - 64 - 15 + 39, -1);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;
        if (button.id == 3) escape();
        else if (button.id == 0) add();
        else if (button.id == 4) delete();
        else if (button.id == 1) login(selectedAccountIndex);
        else if (button.id == 2) offlineLogin(selectedAccountIndex);
        else if (button.id == 7) edit();
        else accountGUI.actionPerformed(button);
    }

    private void reloadSkins() {
        AccountConfig.saveToFile();
        SkinUtils.cacheSkins();
        updateShownSkin();
    }

    private void escape() {
        mc.displayGuiScreen(null);
    }

    private void delete() {
        AccountDatabase.getInstance().getAccounts().remove(getCurrentAsEditable());

        if (selectedAccountIndex > 0) selectedAccountIndex--;

        updateQueried();
        updateButtons();
    }

    public static void add() {
        Minecraft.getMinecraft().displayGuiScreen(new AddAccountGUI());
    }

    private void offlineLogin(Integer selected) {
        ExtendedAccountData data = accounts.get(selected);
        AccountManager.getInstance().setUserOffline(data.alias);

        failedLogin = null;

        Minecraft.getMinecraft().displayGuiScreen(null);
        ExtendedAccountData current = getCurrentAsEditable();

        current.useCount++;
        current.lastUsed = System.currentTimeMillis();
    }

    public void login(Integer selected) {
        ExtendedAccountData data = accounts.get(selected);
        failedLogin = AccountManager.getInstance().setUser(data.user, data.password);

        if (failedLogin == null) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            ExtendedAccountData current = getCurrentAsEditable();

            current.premium = true;
            current.useCount++;
            current.lastUsed = System.currentTimeMillis();
        } else if (HTTPTools.ping("http://minecraft.net"))
            getCurrentAsEditable().premium = false;
    }

    private void edit() {
        mc.displayGuiScreen(new EditAccountGUI(selectedAccountIndex));
    }

    private void updateQueried() {
        accounts = convertData();

        if (!query.equals(I18n.format("accounts.search")) && !query.equals(""))
            for (int i = 0; i < accounts.size(); i++)
                if (!accounts.get(i).alias.toLowerCase().contains(query.toLowerCase())) {
                    accounts.remove(i);
                    i--;
                }

        if (!accounts.isEmpty()) while (selectedAccountIndex >= accounts.size()) selectedAccountIndex--;
    }

    @Override
    protected void keyTyped(char character, int keyIndex) {
        if (keyIndex == Keyboard.KEY_UP && !accounts.isEmpty()) {
            if (selectedAccountIndex > 0) selectedAccountIndex--;
        } else if (keyIndex == Keyboard.KEY_DOWN && !accounts.isEmpty()) {
            if (selectedAccountIndex < accounts.size() - 1) selectedAccountIndex++;
        } else if (keyIndex == Keyboard.KEY_ESCAPE) escape();
        else if (keyIndex == Keyboard.KEY_DELETE && delete.enabled) delete();
        else if (character == '+') add();
        else if (character == '/' && edit.enabled) edit();
        else if (!search.isFocused() && keyIndex == Keyboard.KEY_R) reloadSkins();
        else if (keyIndex == Keyboard.KEY_RETURN && !search.isFocused() && (login.enabled || offlineLogin.enabled)) {
            if ((Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                    && offlineLogin.enabled) offlineLogin(selectedAccountIndex);
            else if (login.enabled) login(selectedAccountIndex);
        } else if (keyIndex == Keyboard.KEY_BACK) {
            if (search.isFocused() && query.length() > 0) {
                query = query.substring(0, query.length() - 1);
                updateText();
                updateQueried();
            }
        } else if (keyIndex == Keyboard.KEY_F5) reloadSkins();
        else if (character != 0) if (search.isFocused()) {
            if (keyIndex == Keyboard.KEY_RETURN) {
                search.setFocused(false);
                updateText();
                updateQueried();
                return;
            }
            query += character;
            updateText();
            updateQueried();
        }
    }

    private ArrayList<ExtendedAccountData> convertData() {
        ArrayList<AccountData> unconverted = new ArrayList<>(getAccountList());
        ArrayList<ExtendedAccountData> converted = new ArrayList<>();

        int index = 0;
        for (AccountData data : unconverted) {
            if (data instanceof ExtendedAccountData) converted.add((ExtendedAccountData) data);
            else {
                converted.add(new ExtendedAccountData(EncryptionTools.decode(data.user), EncryptionTools.decode(data.password), data.alias));
                AccountDatabase.getInstance().getAccounts().set(index, new ExtendedAccountData(EncryptionTools.decode(data.user), EncryptionTools.decode(data.password), data.alias));
            }
            index++;
        }
        return converted;
    }

    private static List<AccountData> getAccountList() {
        return AccountDatabase.getInstance().getAccounts();
    }

    private ExtendedAccountData getCurrentAsEditable() {
        for (AccountData data : getAccountList())
            if (data instanceof ExtendedAccountData)
                if (data.equals(accounts.get(selectedAccountIndex)))
                    return (ExtendedAccountData) data;
        throw new IllegalStateException("Nothing found!");
    }

    private void updateButtons() {
        login.enabled = !accounts.isEmpty() && !EncryptionTools.decode(accounts.get(selectedAccountIndex).password).isEmpty();
        offlineLogin.enabled = !accounts.isEmpty();
        delete.enabled = !accounts.isEmpty();
        edit.enabled = !accounts.isEmpty();
    }

    public class AccountList extends GuiSlot {

        public AccountList(Minecraft minecraft) {
            super(minecraft, AccountSelectorGUI.this.width, AccountSelectorGUI.this.height, 32,
                    AccountSelectorGUI.this.height - 64, 14);
        }

        @Override
        protected int getSize() {
            return accounts.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            selectedAccountIndex = slotIndex;
            updateButtons();

            if (isDoubleClick && login.enabled) login(slotIndex);
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == selectedAccountIndex;
        }

        @Override
        protected int getContentHeight() {
            return accounts.size() * 14;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            ExtendedAccountData data = accounts.get(entryID);
            String alias = data.alias;

            if (StringUtils.isEmpty(alias)) alias = I18n.format("accounts.account", entryID + 1);
            int color = 16777215;
            if (Minecraft.getMinecraft().getSession().getUsername().equals(data.alias)) color = 0x00FF00;

            if (alias.contains("@")) {
                String[] split = alias.split("@");
                alias = Strings.repeat('*', split[0].length());
                alias += "@";
                alias += Strings.repeat('*', split[1].length());
            }

            drawString(fontRendererObj, alias, p_180791_2_ + 2, p_180791_3_ + 1, color);
        }
    }

    private String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(new Date());
    }

    private void drawBorderedRect(int x, int y, int x1, int y1, int size, int borderColor, int insideColor) {
        Gui.drawRect(x + size, y + size, x1 - size, y1 - size, insideColor);
        Gui.drawRect(x + size, y + size, x1, y, borderColor);

        Gui.drawRect(x, y, x + size, y1, borderColor);
        Gui.drawRect(x1, y1, x1 - size, y + size, borderColor);
        Gui.drawRect(x, y1 - size, x1, y1, borderColor);
    }
}
