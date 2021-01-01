package tk.bookyclient.bookyclient.accounts.skins;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import tk.bookyclient.bookyclient.utils.Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkinRender {

    private final File file;
    private DynamicTexture previewTexture;
    private ResourceLocation resourceLocation;
    private final TextureManager textureManager;

    public SkinRender(TextureManager textureManager, File file) {
        this.textureManager = textureManager;
        this.file = file;
    }

    private boolean loadPreview() {
        try {
            BufferedImage image = ImageIO.read(file);
            previewTexture = new DynamicTexture(image);
            resourceLocation = textureManager.getDynamicTextureLocation("accounts", previewTexture);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    public void drawImage(Integer xPosition, Integer yPosition, Integer width, Integer height) {
        if (previewTexture == null) {
            boolean successful = loadPreview();
            if (!successful) {
                Constants.LOGGER.error("Failed to load preview!");
                return;
            }
        }
        previewTexture.updateDynamicTexture();

        textureManager.bindTexture(resourceLocation);
        GlStateManager.color(1, 1, 1, 1);
        Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, 16 * 4, 32 * 4);
    }
}