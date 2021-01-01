package tk.bookyclient.bookyclient.accounts.skins;

import net.minecraft.client.Minecraft;
import tk.bookyclient.bookyclient.accounts.model.AccountData;
import tk.bookyclient.bookyclient.accounts.utils.AccountDatabase;
import tk.bookyclient.bookyclient.utils.Constants;
import tk.bookyclient.bookyclient.utils.UUIDFetcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.UUID;

public class SkinUtils {

    public static final File cacheFolder = new File(Constants.CACHE_DIR, "skins");
    private static final File skinOut = new File(cacheFolder, "temp.png");

    public static void buildSkin(String name) {
        BufferedImage skin;
        try {
            skin = ImageIO.read(new File(cacheFolder, name + ".png"));
        } catch (IOException exception) {
            if (skinOut.exists()) skinOut.delete();
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
            int[] hat = skin.getRGB(40, 8, 8, 8, null, 0, 8);
            int[] jacket = skin.getRGB(20, 36, 8, 12, null, 0, 8);
            int[] armLeft2 = skin.getRGB(44, 36, 4, 12, null, 0, 4);
            int[] armRight2 = skin.getRGB(52, 52, 4, 12, null, 0, 4);
            int[] legLeft2 = skin.getRGB(4, 36, 4, 12, null, 0, 4);
            int[] legRight2 = skin.getRGB(4, 52, 4, 12, null, 0, 4);

            for (int i = 0; i < hat.length; i++) if (hat[i] == 0) hat[i] = head[i];
            for (int i = 0; i < jacket.length; i++) if (jacket[i] == 0) jacket[i] = chest[i];
            for (int i = 0; i < armLeft2.length; i++) if (armLeft2[i] == 0) armLeft2[i] = armLeft[i];
            for (int i = 0; i < armRight2.length; i++) if (armRight2[i] == 0) armRight2[i] = armRight[i];
            for (int i = 0; i < legLeft2.length; i++) if (legLeft2[i] == 0) legLeft2[i] = legLeft[i];
            for (int i = 0; i < legRight2.length; i++) if (legRight2[i] == 0) legRight2[i] = legRight[i];

            drawing.setRGB(4, 0, 8, 8, hat, 0, 8);
            drawing.setRGB(4, 8, 8, 12, jacket, 0, 8);
            drawing.setRGB(0, 8, 4, 12, armLeft2, 0, 4);
            drawing.setRGB(12, 8, 4, 12, armRight2, 0, 4);
            drawing.setRGB(4, 20, 4, 12, legLeft2, 0, 4);
            drawing.setRGB(8, 20, 4, 12, legRight2, 0, 4);
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
            ImageIO.write(drawing, "png", skinOut);
        } catch (IOException exception) {
            throw new Error(exception);
        }
    }

    public static void drawSkin(Integer x, Integer y, Integer width, Integer height) {
        if (!skinOut.exists()) return;

        SkinRender render = new SkinRender(Minecraft.getMinecraft().getTextureManager(), skinOut);
        render.drawImage(x, y, width, height);
    }

    public static void cacheSkins() {
        if (!cacheFolder.exists()) cacheFolder.mkdirs();

        for (AccountData account : AccountDatabase.getInstance().getAccounts()) {
            File file = new File(cacheFolder, account.alias + ".png");
            if (file.exists() || account.alias.contains("@")) continue;

            try {
                UUID uuid = UUIDFetcher.getUUID(account.alias);
                URL url = new URL("https://crafatar.com/skins/" + uuid);
                InputStream input = url.openStream();

                if (file.exists()) file.delete();
                file.createNewFile();
                OutputStream output = new FileOutputStream(file);

                byte[] bytes = new byte[2048];
                int length;
                while ((length = input.read(bytes)) != -1) output.write(bytes, 0, length);

                input.close();
                output.close();
            } catch (Throwable ignored) {
            }
        }
    }
}
