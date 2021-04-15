package tk.bookyclient.bookyclient.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDFetcher {

    private static final Gson GSON = new Gson();
    private static final String URL = "https://playerdb.co/api/player/minecraft/%s";
    private static final List<FetcherData> CACHE = new ArrayList<>();

    public static FetcherData getFromName(String name) {
        try {
            return CACHE.stream().filter(data -> data.getName().equalsIgnoreCase(name)).findAny().orElse(fetch(name));
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static FetcherData getFromUUID(UUID uuid) {
        try {
            return CACHE.stream().filter(data -> data.getUUID().equals(uuid)).findAny().orElse(fetch(uuid.toString()));
        } catch (Throwable throwable) {
            return null;
        }
    }

    public static FetcherData fetch(String argument) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format(URL, argument)).openConnection();
        connection.setRequestProperty("User-Agent", Constants.USER_AGENT);

        JsonObject data = GSON.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), JsonObject.class);
        JsonObject playerJSON = data.getAsJsonObject("data").getAsJsonObject("player");

        FetcherData fetched = new FetcherData(UUID.fromString(playerJSON.getAsJsonPrimitive("id").getAsString()), playerJSON.getAsJsonPrimitive("username").getAsString());
        CACHE.add(fetched);

        return fetched;
    }

    public static class FetcherData {

        private final UUID uuid;
        private final String name;

        public FetcherData(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public UUID getUUID() {
            return uuid;
        }

        public String getName() {
            return name;
        }
    }
}