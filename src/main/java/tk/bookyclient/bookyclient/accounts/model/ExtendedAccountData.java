package tk.bookyclient.bookyclient.accounts.model;

public class ExtendedAccountData extends AccountData {

    public Boolean premium;
    public Long lastUsed;
    public Integer useCount;

    public ExtendedAccountData(String user, String pass, String alias) {
        super(user, pass, alias);

        useCount = 0;
        premium = false;
        lastUsed = System.currentTimeMillis();
    }

    public ExtendedAccountData(String user, String password, String alias, Integer useCount, Long lastUsed, Boolean premium) {
        super(user, password, alias);

        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.premium = premium;
    }
}
