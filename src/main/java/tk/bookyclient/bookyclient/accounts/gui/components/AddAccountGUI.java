package tk.bookyclient.bookyclient.accounts.gui.components;

import net.minecraft.client.resources.I18n;
import tk.bookyclient.bookyclient.accounts.Account;
import tk.bookyclient.bookyclient.accounts.AccountDatabase;
import tk.bookyclient.bookyclient.accounts.gui.main.AbstractAccountGUI;

public class AddAccountGUI extends AbstractAccountGUI {

    public AddAccountGUI() {
        super(I18n.format("accounts.addaccount"));
    }

    @Override
    public void complete() {
        AccountDatabase.getAccounts().add(new Account(getUsername(), getPassword(), getUsername(), false, 0, 0));
    }
}
