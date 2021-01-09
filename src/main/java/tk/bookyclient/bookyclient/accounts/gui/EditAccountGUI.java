package tk.bookyclient.bookyclient.accounts.gui;

import tk.bookyclient.bookyclient.accounts.encryption.EncryptionTools;
import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.accounts.model.ExtendedAccountData;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;

class EditAccountGUI extends AbstractAccountGUI {

    private final String originalUsername;
    private final ExtendedAccountData data;
    private final Integer selectedIndex;

    public EditAccountGUI(Integer index) {
        super("accounts.editaccount");

        selectedIndex = index;
        AccountData data = AccountDatabase.getInstance().getAccounts().get(index);
        originalUsername = EncryptionTools.decode(data.user);

        if (data instanceof ExtendedAccountData) this.data = (ExtendedAccountData) data;
        else this.data = new ExtendedAccountData(data.user, data.password, data.alias, 0, System.currentTimeMillis(), false);
    }

    @Override
    public void initGui() {
        super.initGui();

        setUsername(EncryptionTools.decode(data.user));
        setPassword(EncryptionTools.decode(data.password));
    }

    @Override
    public void complete() {
        AccountDatabase.getInstance().getAccounts().set(selectedIndex, new ExtendedAccountData(getUsername(), getPassword(), hasUserChanged ? getUsername() : data.alias, data.useCount, data.lastUsed, data.premium));
    }

    @Override
    public boolean canComplete() {
        if (getUsername().length() <= 0)
            return false;
        else if (getUsername().equals(originalUsername))
            return true;
        else
            return accountNotInList();
    }
}
