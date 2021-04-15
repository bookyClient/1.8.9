package tk.bookyclient.bookyclient.accounts;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import tk.bookyclient.bookyclient.mixins.client.MinecraftAccessor;

import java.io.Serializable;
import java.util.UUID;

public class Account implements Serializable {

    private static final UserAuthentication AUTHENTICATION = new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), UUID.randomUUID().toString()).createUserAuthentication(Agent.MINECRAFT);

    private String username, password, name;
    private boolean premium;
    private long lastUsed;
    private int useCount;

    public Account(String username, String password, String name, boolean premium, long lastUsed, int useCount) {
        this.username = Accounts.encode(username);
        this.password = Accounts.encode(password);
        this.name = name;
        this.premium = premium;
        this.lastUsed = lastUsed;
        this.useCount = useCount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public boolean login() {
        try {
            boolean offline = false;
            String name = getUsername();

            try {
                name = Accounts.decode(getUsername());
            } catch (Throwable throwable) {
                offline = true;
            }

            MinecraftAccessor client = (MinecraftAccessor) Minecraft.getMinecraft();

            Session session;
            AUTHENTICATION.logOut();

            if (!offline) {
                AUTHENTICATION.setUsername(name);
                AUTHENTICATION.setPassword(Accounts.decode(getPassword()));
                AUTHENTICATION.logIn();

                session = new Session(AUTHENTICATION.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID(AUTHENTICATION.getSelectedProfile().getId()), AUTHENTICATION.getAuthenticatedToken(), AUTHENTICATION.getUserType().getName());
            } else {
                session = new Session(getUsername(), getUsername(), "0", "legacy");
            }

            setPremium(!offline);
            setName(session.getUsername());

            client.setSession(session);
            return false;
        } catch (Throwable throwable) {
            return true;
        }
    }
}
