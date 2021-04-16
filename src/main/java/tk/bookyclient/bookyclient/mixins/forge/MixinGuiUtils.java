package tk.bookyclient.bookyclient.mixins.forge;
// Created by booky10 in bookyClient (21:03 16.04.21)

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect;

@Mixin(GuiUtils.class)
public abstract class MixinGuiUtils {

    private static int y = 0;
    private static boolean allowed;

    @Inject(method = "drawHoveringText", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preTooltipRender(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font, CallbackInfo callbackInfo) {
        if (textLines.isEmpty()) return;

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        int tooltipTextWidth = 0;

        for (String textLine : textLines) {
            int textLineWidth = font.getStringWidth(textLine);
            if (textLineWidth <= tooltipTextWidth) continue;
            tooltipTextWidth = textLineWidth;
        }

        boolean needsWrap = false;

        int titleLinesCount = 1, tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) {
                if (mouseX > screenWidth / 2) {
                    tooltipTextWidth = mouseX - 12 - 8;
                } else {
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                }
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<>();

            for (int i = 0; i < textLines.size(); i++) {
                String textLine = textLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                if (i == 0) titleLinesCount = wrappedLine.size();

                for (String line : wrappedLine) {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth) wrappedTooltipWidth = lineWidth;
                    wrappedTextLines.add(line);
                }
            }

            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (mouseX > screenWidth / 2) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
            } else {
                tooltipX = mouseX + 12;
            }
        }

        int tooltipY = mouseY - 12, tooltipHeight = 8;
        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount) tooltipHeight += 2;
        }

        if (tooltipY + tooltipHeight + 6 > screenHeight) {
            tooltipY = screenHeight - tooltipHeight - 6;
        }

        if (ClientSettings.getInstance().scrollableTooltips) {
            GlStateManager.pushMatrix();
            if (!allowed) y = 0;
            if (allowed = tooltipY < 0) {
                int wheel = Mouse.getDWheel();

                if (wheel != 0) {
                    if (wheel < 0) {
                        y -= 10;
                    } else {
                        y += 10;
                    }

                    if (y + tooltipY > 6) {
                        y = -tooltipY + 6;
                    } else {
                        if (y + tooltipY + tooltipHeight + 6 < screenHeight) {
                            y = screenHeight - 6 - tooltipY - tooltipHeight;
                        }
                    }
                }
            }
            GlStateManager.translate(0, y, 0);
        }

        int zLevel = 300, backgroundColor = 0xF0100010;
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);

        int borderColorStart = 0x505000FF, borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
            String line = textLines.get(lineNumber);
            font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);
            if (lineNumber + 1 == titleLinesCount) tooltipY += 2;
            tooltipY += 10;
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();

        GlStateManager.enableRescaleNormal();
        if (ClientSettings.getInstance().scrollableTooltips) GlStateManager.popMatrix();

        callbackInfo.cancel();
    }
}