package tk.bookyclient.bookyclient.accounts.utils;

import tk.bookyclient.bookyclient.accounts.model.AccountData;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountDatabase implements Serializable {

    private static AccountDatabase instance;
    private final ArrayList<AccountData> altList;

    private AccountDatabase() {
        altList = new ArrayList<>();
    }

    private static void loadFromConfig() {
        if (instance == null) instance = (AccountDatabase) AccountConfig.getInstance().getKey("altaccounts");
    }

    private static void saveToConfig() {
        AccountConfig.getInstance().setKey("altaccounts", instance);
    }

    public static AccountDatabase getInstance() {
        loadFromConfig();

        if (instance == null) {
            instance = new AccountDatabase();
            saveToConfig();
        }
        return instance;
    }

    public ArrayList<AccountData> getAccounts() {
        return altList;
    }
}
