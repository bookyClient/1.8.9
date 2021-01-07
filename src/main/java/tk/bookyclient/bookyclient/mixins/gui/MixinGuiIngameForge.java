package tk.bookyclient.bookyclient.mixins.gui;
// Created by booky10 in bookyClient (22:44 04.01.21)

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.PLAYER_LIST;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends GuiIngame {

    @SuppressWarnings("unused")
    public MixinGuiIngameForge(Minecraft mcIn) {
        super(mcIn);
    }

    @Shadow(remap = false)
    protected abstract void post(RenderGameOverlayEvent.ElementType type);

    @Shadow(remap = false)
    protected abstract boolean pre(RenderGameOverlayEvent.ElementType type);

    /**
     * @author booky10
     */
    @Overwrite(remap = false)
    protected void renderPlayerList(int width, int height) {
        ScoreObjective scoreobjective = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0);

        if (mc.gameSettings.keyBindPlayerList.isKeyDown()) {
            overlayPlayerList.updatePlayerList(true);
            if (pre(PLAYER_LIST)) return;

            overlayPlayerList.renderPlayerlist(width, mc.theWorld.getScoreboard(), scoreobjective);

            post(PLAYER_LIST);
        } else
            overlayPlayerList.updatePlayerList(false);
    }
}