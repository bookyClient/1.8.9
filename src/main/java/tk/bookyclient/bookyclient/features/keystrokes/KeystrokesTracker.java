package tk.bookyclient.bookyclient.features.keystrokes;
// Created by booky10 in bookyClient (20:50 18.09.20)

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;
import tk.bookyclient.bookyclient.features.keystrokes.render.KeystrokesRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class KeystrokesTracker {

    private static final KeystrokesRenderer renderer = KeystrokesUtils.renderer;
    private static final List<Long> leftClicks = new ArrayList<>(), rightClicks = new ArrayList<>();
    private static boolean leftWasDown = false, rightWasDown = false;

    public static int getLeftCPS() {
        long time = System.currentTimeMillis();
        leftClicks.removeIf(clickTime -> clickTime + 1000L < time);
        return leftClicks.size();
    }

    public static int getRightCPS() {
        long time = System.currentTimeMillis();
        rightClicks.removeIf(clickTime -> clickTime + 1000L < time);
        return rightClicks.size();
    }

    public static void tickCPS() {
        boolean downNow = Mouse.isButtonDown(renderer.getMouseButtons()[0].getButton());

        if (downNow != leftWasDown && downNow) leftClicks.add(System.currentTimeMillis());
        leftWasDown = downNow;
        downNow = Mouse.isButtonDown(renderer.getMouseButtons()[1].getButton());

        if (downNow != rightWasDown && downNow) rightClicks.add(System.currentTimeMillis());
        rightWasDown = downNow;
    }

    private static String display = I18n.format("keystrokes.reach.none");
    private static Long lastAttack = 0L;

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final DecimalFormat formatter = new DecimalFormat("0.0");

    public static String getReach() {
        return display;
    }

    public static void tickReach() {
        if (System.nanoTime() - lastAttack >= 2.0E9)
            display = I18n.format("keystrokes.reach.none");
    }

    public static void updateReach() {
        Vec3 playerPosition = mc.getRenderViewEntity().getPositionEyes(1.0F);
        double reach = mc.objectMouseOver.hitVec.distanceTo(playerPosition);

        display = I18n.format("keystrokes.reach.format", formatter.format(reach));
        lastAttack = System.nanoTime();
    }
}