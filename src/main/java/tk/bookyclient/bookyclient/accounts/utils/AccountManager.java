package tk.bookyclient.bookyclient.accounts.utils;

import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import tk.bookyclient.bookyclient.accounts.encryption.EncryptionTools;
import tk.bookyclient.bookyclient.accounts.exceptions.AlreadyLoggedInException;
import tk.bookyclient.bookyclient.accounts.model.AccountData;

import java.lang.reflect.Field;
import java.util.UUID;

public class AccountManager {

    private static AccountManager manager = null;
    private final UserAuthentication auth;

    private AccountManager() {
        UUID uuid = UUID.randomUUID();

        AuthenticationService service = new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), uuid.toString());
        auth = service.createUserAuthentication(Agent.MINECRAFT);

        service.createMinecraftSessionService();
    }

    public static AccountManager getInstance() {
        if (manager == null) manager = new AccountManager();
        return manager;
    }

    public Throwable setUser(String username, String password) {
        AccountDatabase database = AccountDatabase.getInstance();
        Throwable error = null;

        if (!Minecraft.getMinecraft().getSession().getUsername().equals(EncryptionTools.decode(username)) || Minecraft.getMinecraft().getSession().getToken().equals("0")) {
            if (!Minecraft.getMinecraft().getSession().getToken().equals("0"))
                for (AccountData data : database.getAccounts())
                    if (data.alias.equals(Minecraft.getMinecraft().getSession().getUsername()) && data.user.equals(username)) {
                        error = new AlreadyLoggedInException();
                        return error;
                    }

            auth.logOut();
            auth.setUsername(EncryptionTools.decode(username));
            auth.setPassword(EncryptionTools.decode(password));

            try {
                auth.logIn();
                Session session = new Session(auth.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID(auth.getSelectedProfile().getId()), auth.getAuthenticatedToken(), auth.getUserType().getName());
                setSession(session);

                for (int i = 0; i < database.getAccounts().size(); i++) {
                    AccountData data = database.getAccounts().get(i);
                    if (data.user.equals(username) && data.password.equals(password))
                        data.alias = session.getUsername();
                }
            } catch (Throwable throwable) {
                error = throwable;
            }
        }
        return error;
    }

    public void setUserOffline(String username) {
        try {
            username = EncryptionTools.decode(username);
            auth.logOut();
            Session session = new Session(username, username, "0", "legacy");
            setSession(session);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }

    private void setSession(Session session) throws ReflectiveOperationException {
        Field field = Minecraft.class.getDeclaredField("session");
        field.setAccessible(true);
        field.set(Minecraft.getMinecraft(), session);
    }
}
