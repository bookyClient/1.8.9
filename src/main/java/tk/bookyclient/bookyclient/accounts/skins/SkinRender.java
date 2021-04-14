package tk.bookyclient.bookyclient.accounts.skins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkinRender {

    private final TextureManager manager;
    private final File file;
    private ResourceLocation location;
    private DynamicTexture previewTexture;

    public SkinRender(TextureManager manager, File file) {
        this.manager = manager;
        this.file = file;
    }

    public void draw(Integer xPosition, Integer yPosition, Integer width, Integer height) {
        if (previewTexture == null && !load()) return;

        previewTexture.updateDynamicTexture();
        manager.bindTexture(location);

        GlStateManager.color(1, 1, 1, 1);
        Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, 16 * 4, 32 * 4);
    }

    private boolean load() {
        try {
            BufferedImage image = ImageIO.read(file);
            previewTexture = new DynamicTexture(image);
            location = manager.getDynamicTextureLocation("accounts", previewTexture);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}