package tk.bookyclient.bookyclient.mixins;
// Created by booky10 in bookyClient (15:23 07.01.21)

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Shadow
    public abstract NetworkManager getNetworkManager();

    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    public void onHandleResourcePack(S48PacketResourcePackSend packetIn, CallbackInfo callbackInfo) {
        if (!validateResourcePackUrl(packetIn.getURL(), packetIn.getHash())) callbackInfo.cancel();
    }

    private boolean validateResourcePackUrl(String url, String hash) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            boolean isLevelProtocol = scheme.equals("level");

            if (!scheme.equals("http") && !scheme.equals("https") && !isLevelProtocol) {
                getNetworkManager().sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring(8), StandardCharsets.UTF_8.toString());
            if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                Constants.LOGGER.warn("Malicious server tried to access " + url + "!");
                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }
            return true;
        } catch (URISyntaxException exception) {
            return false;
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        }

        return false;
    }
}