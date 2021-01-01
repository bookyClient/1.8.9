package tk.bookyclient.bookyclient.accounts.model;

import tk.bookyclient.bookyclient.accounts.encryption.EncryptionTools;

import java.io.Serializable;

public class AccountData implements Serializable {

    public final String user, password;
    public String alias;

    protected AccountData(String user, String password, String alias) {
        this.user = EncryptionTools.encode(user);
        this.password = EncryptionTools.encode(password);
        this.alias = alias;
    }

    public boolean equalsBasic(Object object) {
        if (this == object)
            return true;
        else if (object == null)
            return false;
        else if (getClass() != object.getClass())
            return false;
        else {
            AccountData other = (AccountData) object;
            return user.equals(other.user);
        }
    }
}
