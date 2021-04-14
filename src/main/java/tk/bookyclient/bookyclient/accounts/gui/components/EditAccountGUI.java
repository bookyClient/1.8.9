package tk.bookyclient.bookyclient.accounts.gui.components;

import net.minecraft.client.resources.I18n;
import tk.bookyclient.bookyclient.accounts.Account;
import tk.bookyclient.bookyclient.accounts.AccountDatabase;
import tk.bookyclient.bookyclient.accounts.Accounts;
import tk.bookyclient.bookyclient.accounts.gui.main.AbstractAccountGUI;

public class EditAccountGUI extends AbstractAccountGUI {

    private final String username;
    private final Account account;
    private final int selected;

    public EditAccountGUI(int selected) {
        super(I18n.format("accounts.editaccount"));
        account = AccountDatabase.getAccounts().get(selected);

        String username;
        try {
            username = Accounts.decode(account.getUsername());
        } catch (Throwable throwable) {
            account.setPremium(false);
            username = account.getUsername();
        }

        this.username = username;
        this.selected = selected;
    }

    @Override
    public void initGui() {
        super.initGui();

        try {
            setUsername(Accounts.decode(account.getUsername()));
        } catch (Throwable throwable) {
            account.setPremium(false);
            setUsername(account.getUsername());
        }

        if (account.getPassword().isEmpty()) return;
        setPassword(Accounts.decode(account.getPassword()));
    }

    @Override
    public void complete() {
        account.setUsername(getUsername());
        account.setPassword(getPassword());
        account.setName(hasChanged ? account.getUsername() : account.getName());

        AccountDatabase.getAccounts().set(selected, account);
        Accounts.save();
    }

    @Override
    public boolean canComplete() {
        return super.canComplete() && !getUsername().equals(username);
    }
}
