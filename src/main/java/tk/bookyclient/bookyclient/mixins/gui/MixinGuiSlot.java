package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (17:14 16.04.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.bookyclient.bookyclient.settings.ClientSettings;
import tk.bookyclient.bookyclient.utils.Constants;

@Mixin(GuiSlot.class)
public abstract class MixinGuiSlot {

    @Shadow public int bottom, top;
    @Shadow protected float amountScrolled;

    private float target;
    private long start, duration;

    @Shadow
    public abstract int func_148135_f();

    @Shadow
    public abstract boolean getEnabled();

    @Shadow
    protected abstract int getScrollBarX();

    @Shadow
    protected abstract int getContentHeight();

    @Shadow
    public abstract int getAmountScrolled();

    @Shadow
    protected abstract void func_148142_b(int p_148142_1_, int p_148142_2_);

    @Shadow @Final protected Minecraft mc;

    public void scrollTo(float value, boolean animated, long duration) {
        target = Constants.UTILITIES.clamp(value, func_148135_f());

        if (animated) {
            start = System.currentTimeMillis();
            this.duration = duration;
        } else {
            amountScrolled = target;
        }
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiSlot;bindAmountScrolled()V"))
    public void onScrollBind(GuiSlot gui) {
        if (ClientSettings.getInstance().smoothScrolling) {
            amountScrolled = Constants.UTILITIES.clamp(amountScrolled, func_148135_f());
            target = Constants.UTILITIES.clamp(target, func_148135_f());
        } else {
            gui.bindAmountScrolled();
        }
    }

    @Inject(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", remap = false), cancellable = true)
    public void onWheelGet(CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().smoothScrolling) return;
        if (Mouse.isButtonDown(0) && getEnabled()) {
            target = amountScrolled = Constants.UTILITIES.clamp(amountScrolled, func_148135_f(), 0);
        } else {
            int wheel = Mouse.getEventDWheel();
            if (wheel != 0) scrollTo(target + 19 * (wheel > 0 ? -1 : 1), true, 600);
            callbackInfo.cancel();
        }
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    public void preRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().smoothScrolling) return;
        float[] target = new float[]{this.target};
        amountScrolled = Constants.UTILITIES.easeScroll(target, amountScrolled, func_148135_f(), 20f / Minecraft.getDebugFPS(), (double) start, (double) duration);
        this.target = target[0];
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiSlot;func_148135_f()I", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    public void onScrollbarRender(int mouseX, int mouseY, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClientSettings.getInstance().smoothScrolling) return;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();

        int minX = getScrollBarX(), maxX = minX + 6, maxScroll = func_148135_f();
        if (maxScroll > 0) {
            int height = MathHelper.clamp_int((bottom - top) * (bottom - top) / getContentHeight(), 32, bottom - top - 8);
            height = (int) ((double) height - Math.min(amountScrolled < 0.0D ? (int) (-amountScrolled) : (amountScrolled > (double) func_148135_f() ? (int) amountScrolled - func_148135_f() : 0), (double) height * 0.75D));
            int minY = Math.min(Math.max(getAmountScrolled() * (bottom - top - height) / maxScroll + top, top), bottom - height);

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

        func_148142_b(mouseX, mouseY);

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();

        callbackInfo.cancel();
    }
}