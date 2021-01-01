package tk.bookyclient.bookyclient.accounts.gui;

import net.minecraft.client.resources.I18n;
import tk.bookyclient.bookyclient.accounts.model.ExtendedAccountData;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;

public class AddAccountGUI extends AbstractAccountGUI {

    public AddAccountGUI() {
        super(I18n.format("accounts.addaccount"));
    }

    @Override
    public void complete() {
        ExtendedAccountData data = new ExtendedAccountData(getUsername(), getPassword(), getUsername());
        AccountDatabase.getInstance().getAccounts().add(data);
    }
}
