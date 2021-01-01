package tk.bookyclient.bookyclient.accounts.utils;

import tk.bookyclient.bookyclient.accounts.model.AccountData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountDatabase implements Serializable {

    private static AccountDatabase instance;
    private final List<AccountData> accounts = new ArrayList<>();

    private AccountDatabase() {
    }

    private static void loadFromConfig() {
        if (instance != null) return;
        instance = (AccountDatabase) AccountConfig.getInstance().getKey("accounts");
    }

    private static void saveToConfig() {
        AccountConfig.getInstance().setKey("accounts", instance);
    }

    public static AccountDatabase getInstance() {
        loadFromConfig();

        if (instance == null) {
            instance = new AccountDatabase();
            saveToConfig();
        }

        return instance;
    }

    public List<AccountData> getAccounts() {
        return accounts;
    }
}
