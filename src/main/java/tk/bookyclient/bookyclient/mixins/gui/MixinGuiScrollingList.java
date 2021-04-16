package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (17:08 16.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(GuiScrollingList.class)
public abstract class MixinGuiScrollingList {

    @Shadow(remap = false) @Final protected int bottom, top, left, listWidth;
    @Shadow(remap = false) private float scrollDistance;

    @Shadow(remap = false)
    protected abstract int getContentHeight();

    @Shadow(remap = false)
    protected abstract void drawScreen(int mouseX, int mouseY);

    private float target;
    private long start, duration;
    private Method applyScrollLimits;

    private int getMaxScrollHeight() {
        int max = getContentHeight() - (bottom - top) + 4;
        if (max < 0) max /= 2;
        return max;
    }

    public void scrollTo(float value, boolean animated, long duration) {
        target = Constants.UTILITIES.clamp(value, getMaxScrollHeight());

        if (animated) {
            start = System.currentTimeMillis();
            this.duration = duration;
        } else {
            scrollDistance = target;
        }
    }

    @Redirect(method = "drawScreen(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/GuiScrollingList;applyScrollLimits()V", remap = false), remap = false)
    public void onScrollBind(GuiScrollingList gui) throws InvocationTargetException, IllegalAccessException {
        if (ClientSettings.getInstance().smoothScrolling) {
            if (!Mouse.isButtonDown(0)) return;
            target = scrollDistance = Constants.UTILITIES.clamp(scrollDistance, getMaxScrollHeight(), 0);
        } else {
            if (applyScrollLimits == null) applyScrollLimits = ReflectionHelper.findMethod(GuiScrollingList.class, gui, new String[]{"applyScrollLimits"});
            applyScrollLimits.invoke(gui);
        }
    }

    @Redirect(method = "drawScreen(IIF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", ordinal = 0, remap = false), remap = false)
    public int onMouseScroll(int mouseX, int mouseY, float partialTicks) {
        if (!ClientSettings.getInstance().smoothScrolling) return Mouse.getEventDWheel();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) scrollTo(target + 19 * (wheel > 0 ? -1 : 1), true, 600);
        return 0;
    }

    @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"), remap = false)
    public void preRender(int int_1, int int_2, float delta, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().smoothScrolling) return;
        float[] target = new float[]{this.target};
        scrollDistance = Constants.UTILITIES.easeScroll(target, scrollDistance, getMaxScrollHeight(), 20f / Minecraft.getDebugFPS(), (double) start, (double) duration);
        this.target = target[0];
    }

    @Inject(method = "drawScreen(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/GuiScrollingList;getContentHeight()I", ordinal = 2, shift = At.Shift.AFTER, remap = false), cancellable = true, remap = false)
    public void onScrollbarRender(int int_1, int int_2, float float_1, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().smoothScrolling) return;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();

        int maxX = left + listWidth, minX = maxX - 6, maxScroll = getMaxScrollHeight();
        if (maxScroll > 0) {
            int height = MathHelper.clamp_int((bottom - top) * (bottom - top) / getContentHeight(), 32, bottom - top - 8);
            height = (int) ((double) height - Math.min(scrollDistance < 0.0D ? (int) (-scrollDistance) : (scrollDistance > (double) getMaxScrollHeight() ? (int) scrollDistance - getMaxScrollHeight() : 0), (double) height * 0.75D));
            int minY = Math.min(Math.max(((int) scrollDistance) * (bottom - top - height) / maxScroll + top, top), bottom - height);

            GlStateManager.disableTexture2D();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(minX, bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            buffer.pos(maxX, bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            buffer.pos(maxX, top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            buffer.pos(minX, top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();

            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(minX, minY + height, 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            buffer.pos(maxX, minY + height, 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            buffer.pos(maxX, minY, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            buffer.pos(minX, minY, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            tessellator.draw();

            buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(minX, minY + height - 1, 0.0D).tex(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            buffer.pos(maxX - 1, minY + height - 1, 0.0D).tex(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
            buffer.pos(maxX - 1, minY, 0.0D).tex(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            buffer.pos(minX, minY, 0.0D).tex(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
            tessellator.draw();
        }

        drawScreen(int_1, int_2);

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        callbackInfo.cancel();
    }
}