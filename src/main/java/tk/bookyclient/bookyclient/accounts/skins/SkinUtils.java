package tk.bookyclient.bookyclient.accounts.skins;

import net.minecraft.client.Minecraft;
import tk.bookyclient.bookyclient.accounts.Account;
import tk.bookyclient.bookyclient.accounts.AccountDatabase;
import tk.bookyclient.bookyclient.utils.Constants;
import tk.bookyclient.bookyclient.utils.UUIDFetcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class SkinUtils {

    private static final File SKIN_CACHE_DIR = new File(Constants.CACHE_DIR, "skins");
    private static final File SKIN_CACHE_FILE = new File(SKIN_CACHE_DIR, "temp.png");

    private static final Object cacheLock = new Object();

    public static void buildSkin(String name) {
        BufferedImage skin;

        try {
            skin = ImageIO.read(new File(SKIN_CACHE_DIR, name + ".png"));
        } catch (IOException exception) {
            if (SKIN_CACHE_FILE.exists()) SKIN_CACHE_FILE.delete();
            return;
        }

        BufferedImage drawing = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
        if (skin.getHeight() == 64) {
            int[] head = skin.getRGB(8, 8, 8, 8, null, 0, 8);
            int[] chest = skin.getRGB(20, 20, 8, 12, null, 0, 8);
            int[] armLeft = skin.getRGB(44, 20, 4, 12, null, 0, 4);
            int[] armRight = skin.getRGB(36, 52, 4, 12, null, 0, 4);
            int[] legLeft = skin.getRGB(4, 20, 4, 12, null, 0, 4);
            int[] legRight = skin.getRGB(20, 52, 4, 12, null, 0, 4);

            int[] headOverlay = skin.getRGB(40, 8, 8, 8, null, 0, 8);
            int[] chestOverlay = skin.getRGB(20, 36, 8, 12, null, 0, 8);
            int[] armLeftOverlay = skin.getRGB(44, 36, 4, 12, null, 0, 4);
            int[] armRightOverlay = skin.getRGB(52, 52, 4, 12, null, 0, 4);
            int[] legLeftOverlay = skin.getRGB(4, 36, 4, 12, null, 0, 4);
            int[] legRightOverlay = skin.getRGB(4, 52, 4, 12, null, 0, 4);

            for (int i = 0; i < headOverlay.length; i++) if (headOverlay[i] == 0) headOverlay[i] = head[i];
            for (int i = 0; i < chestOverlay.length; i++) if (chestOverlay[i] == 0) chestOverlay[i] = chest[i];
            for (int i = 0; i < armLeftOverlay.length; i++) if (armLeftOverlay[i] == 0) armLeftOverlay[i] = armLeft[i];
            for (int i = 0; i < armRightOverlay.length; i++) if (armRightOverlay[i] == 0) armRightOverlay[i] = armRight[i];
            for (int i = 0; i < legLeftOverlay.length; i++) if (legLeftOverlay[i] == 0) legLeftOverlay[i] = legLeft[i];
            for (int i = 0; i < legRightOverlay.length; i++) if (legRightOverlay[i] == 0) legRightOverlay[i] = legRight[i];

            drawing.setRGB(4, 0, 8, 8, headOverlay, 0, 8);
            drawing.setRGB(4, 8, 8, 12, chestOverlay, 0, 8);
            drawing.setRGB(0, 8, 4, 12, armLeftOverlay, 0, 4);
            drawing.setRGB(12, 8, 4, 12, armRightOverlay, 0, 4);
            drawing.setRGB(4, 20, 4, 12, legLeftOverlay, 0, 4);
            drawing.setRGB(8, 20, 4, 12, legRightOverlay, 0, 4);
        } else {
            int[] head = skin.getRGB(8, 8, 8, 8, null, 0, 8);
            int[] chest = skin.getRGB(20, 20, 8, 12, null, 0, 8);
            int[] arm = skin.getRGB(44, 20, 4, 12, null, 0, 4);
            int[] leg = skin.getRGB(4, 20, 4, 12, null, 0, 4);
            int[] hat = skin.getRGB(40, 8, 8, 8, null, 0, 8);

            for (int i = 0; i < hat.length; i++) if (hat[i] == 0) hat[i] = head[i];

            drawing.setRGB(4, 0, 8, 8, hat, 0, 8);
            drawing.setRGB(4, 8, 8, 12, chest, 0, 8);
            drawing.setRGB(0, 8, 4, 12, arm, 0, 4);
            drawing.setRGB(12, 8, 4, 12, arm, 0, 4);
            drawing.setRGB(4, 20, 4, 12, leg, 0, 4);
            drawing.setRGB(8, 20, 4, 12, leg, 0, 4);
        }

        try {
            ImageIO.write(drawing, "png", SKIN_CACHE_FILE);
        } catch (IOException exception) {
            throw new Error(exception);
        }
    }

    public static void draw(int x, int y, int width, int height) {
        if (!SKIN_CACHE_FILE.exists()) return;

        SkinRender render = new SkinRender(Minecraft.getMinecraft().getTextureManager(), SKIN_CACHE_FILE);
        render.draw(x, y, width, height);
    }

    public static void cacheSkins() {
        SKIN_CACHE_DIR.mkdirs();

        for (Account account : AccountDatabase.getAccounts()) {
            if (account.getName().contains("@")) continue;

            File file = new File(SKIN_CACHE_DIR, account.getName() + ".png");
            if (file.exists()) continue;

            try {
                UUIDFetcher.FetcherData data = UUIDFetcher.getFromName(account.getName());
                if (data == null) return;

                URL url = new URL("https://crafatar.com/skins/" + data.getUUID());
                InputStream input = url.openStream();

                if (file.exists()) file.delete();
                file.createNewFile();

                OutputStream output = new FileOutputStream(file);
                byte[] bytes = new byte[2048];

                int length;
                while ((length = input.read(bytes)) != -1) output.write(bytes, 0, length);

                input.close();
                output.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
