package tk.bookyclient.bookyclient.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class UUIDFetcher {

    private static final Gson GSON = new Gson();
    private static final String URL = "https://playerdb.co/api/player/minecraft/%s";

    public static UUID getUUID(String name) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format(URL, name.replace(' ', '_'))).openConnection();
            connection.setRequestProperty("User-Agent", "MC/" + Minecraft.getMinecraft().getVersion() + "/" + Constants.MOD_NAME + "/" + Constants.VERSION + "/UUIDFetcher");
            JsonObject data = GSON.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JsonObject.class);
            return UUID.fromString(data.getAsJsonObject("data").getAsJsonObject("player").getAsJsonPrimitive("id").getAsString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static String getName(UUID uuid) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format(URL, uuid.toString())).openConnection();
            connection.setRequestProperty("User-Agent", "MC/" + Minecraft.getMinecraft().getVersion() + "/" + Constants.MOD_NAME + "/" + Constants.VERSION + "/UUIDFetcher");
            JsonObject data = GSON.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JsonObject.class);
            return data.getAsJsonObject("data").getAsJsonObject("player").getAsJsonPrimitive("username").getAsString();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}