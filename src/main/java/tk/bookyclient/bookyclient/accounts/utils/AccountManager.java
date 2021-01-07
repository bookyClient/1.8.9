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

    private static AccountManager manager;
    private final UserAuthentication auth;
    private static final Minecraft minecraft = Minecraft.getMinecraft();

    private AccountManager() {
        UUID uuid = UUID.randomUUID();

        AuthenticationService service = new YggdrasilAuthenticationService(minecraft.getProxy(), uuid.toString());
        auth = service.createUserAuthentication(Agent.MINECRAFT);

        service.createMinecraftSessionService();
    }

    public static AccountManager getInstance() {
        if (manager == null) manager = new AccountManager();
        return manager;
    }

    public Throwable setUser(String username, String password) {
        AccountDatabase database = AccountDatabase.getInstance();
        String decodedName;
        try {
            decodedName = EncryptionTools.decode(username);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            decodedName = username;
        }

        if (!minecraft.getSession().getUsername().equals(decodedName) || isOffline()) {
            if (!isOffline())
                for (AccountData data : database.getAccounts())
                    if (data.alias.equals(minecraft.getSession().getUsername()) && data.user.equals(username))
                        return new AlreadyLoggedInException();

            auth.logOut();
            auth.setUsername(decodedName);
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
                return throwable;
            }
        }
        return null;
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

    private boolean isOffline() {
        String token = minecraft.getSession().getToken();
        return token.equals("0") || token.equals("FML") || token.equals("dev");
    }

    private void setSession(Session session) throws ReflectiveOperationException {
        for (Field field : minecraft.getClass().getDeclaredFields()) {
            if (!field.getType().isInstance(session)) continue;

            field.setAccessible(true);
            field.set(minecraft, session);
            break;
        }
    }
}
