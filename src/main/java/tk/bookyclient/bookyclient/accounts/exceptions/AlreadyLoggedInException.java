package tk.bookyclient.bookyclient.accounts.exceptions;

import net.minecraft.client.resources.I18n;

public class AlreadyLoggedInException extends Exception {

    @Override
    public String getLocalizedMessage() {
        return I18n.format("accounts.alreadylogged");
    }
}
