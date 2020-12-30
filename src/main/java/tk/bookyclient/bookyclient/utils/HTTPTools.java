package tk.bookyclient.bookyclient.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class HTTPTools {

    public static boolean ping(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}
