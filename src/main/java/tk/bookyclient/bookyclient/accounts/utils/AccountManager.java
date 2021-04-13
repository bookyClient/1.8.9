package tk.bookyclient.bookyclient.accounts.utils;

import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import tk.bookyclient.bookyclient.accounts.encryption.EncryptionTools;
import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.mixins.client.MinecraftAccessor;

import java.util.UUID;

public class AccountManager {

    private static AccountManager manager;
    private final UserAuthentication auth;
    private static final Minecraft minecraft = Minecraft.getMinecraft();

    private AccountManager() {
        AuthenticationService service = new YggdrasilAuthenticationService(minecraft.getProxy(), UUID.randomUUID().toString());
        auth = service.createUserAuthentication(Agent.MINECRAFT);
        service.createMinecraftSessionService();
    }

    public static AccountManager getInstance() {
        if (manager == null) return (manager = new AccountManager());
        else return manager;
    }

    public Throwable setUser(String username, String password) {
        AccountDatabase database = AccountDatabase.getInstance();
        String name;

        try {
            name = EncryptionTools.decode(username);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            name = username;
        }

        auth.logOut();
        auth.setUsername(name);
        auth.setPassword(EncryptionTools.decode(password));

        try {
            auth.logIn();
            Session session = new Session(auth.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID(auth.getSelectedProfile().getId()), auth.getAuthenticatedToken(), auth.getUserType().getName());
            setSession(session);

            for (int i = 0; i < database.getAccounts().size(); i++) {
                AccountData data = database.getAccounts().get(i);

                if (!data.user.equals(username) || !data.password.equals(password)) continue;
                data.alias = session.getUsername();
            }
            return null;
        } catch (Throwable throwable) {
            return throwable;
        }
    }

    public void setUserOffline(String username) {
        auth.logOut();
        Session session = new Session(username, username, "0", "legacy");

        try {
            setSession(session);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void setSession(Session session) {
        ((MinecraftAccessor) minecraft).setSession(session);
    }
}
