package tk.bookyclient.bookyclient.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountDatabase implements Serializable {

    private static AccountDatabase instance;
    private final ArrayList<Account> accounts = new ArrayList<>();

    public static AccountDatabase getInstance() {
        return instance;
    }

    public static List<Account> getAccounts() {
        return instance.accounts;
    }

    public void setInstance() {
        instance = this;
    }
}
