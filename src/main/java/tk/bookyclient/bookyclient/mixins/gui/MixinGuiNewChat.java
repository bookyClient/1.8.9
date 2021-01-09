package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (12:09 09.01.21)

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tk.bookyclient.bookyclient.settings.ClientSettings;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat extends Gui {

    private static final ClientSettings settings = ClientSettings.getInstance();

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"), expect = 3)
    public void onDrawRect(int left, int top, int right, int bottom, int color) {
        if (settings.clearChatBackground) return;
        drawRect(left, top, right, bottom, color);
    }
}