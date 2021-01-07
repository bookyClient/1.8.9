package tk.bookyclient.bookyclient.utils.gui;
// Created by booky10 in bookyClient (21:04 04.01.21)

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;

public abstract class ClientColorSettingGUI extends ClientScreenGUI {

    private final GuiScreen parentGuiScreen;
    protected final String name;
    protected final Boolean usingOpacity = true;

    private GuiSlider red, green, blue, opacity;
    private GuiButton chroma, activated;

    private final String screenPrefix = I18n.format("client.gui.prefix") + " - ";
    private final String onI18n = I18n.format("options.on");
    private final String offI18n = I18n.format("options.off");

    public ClientColorSettingGUI(GuiScreen parentGuiScreen, String name) {
        this.parentGuiScreen = parentGuiScreen;
        this.name = name;

        initToggles();
        initSliders();
    }

    public ClientColorSettingGUI(GuiScreen parentGuiScreen, String name, int opacityMax) {
        this.parentGuiScreen = parentGuiScreen;
        this.name = name;

        initToggles();
        initSliders();
    }

    private void initToggles() {
        if (isActivated() != null) {
            activated = new GuiButton(101, width / 2 + 5, height / 6 - 6, 150, 20, name + ": " + (isActivated() ? onI18n : offI18n));
        }

        if (hasChroma() != null) {
            chroma = new GuiButton(102, width / 2 + 5, height / 6 + 18, 150, 20, I18n.format("client.colors.chroma") + " " + (hasChroma() ? onI18n : offI18n));
        }
    }

    private void initSliders() {
        if (getRed() != null) {
            red = new GuiSlider(
                    103,
                    width / 2 - 155, height / 6 + 42,
                    150, 20,
                    I18n.format("client.colors.red") + " ", "",
                    0, 255, getRed(),
                    false, true) {

                @Override
                public void updateSlider() {
                    if (getValueInt() < 0) setValue(0);
                    if (getValueInt() > 255) setValue(255);

                    setRed(getValueInt());
                }
            };
        }

        if (getGreen() != null) {
            green = new GuiSlider(
                    104,
                    width / 2 + 5, height / 6 + 42,
                    150, 20,
                    I18n.format("client.colors.green") + " ", "",
                    0, 255, getGreen(),
                    false, true) {

                @Override
                public void updateSlider() {
                    if (getValueInt() < 0) setValue(0);
                    if (getValueInt() > 255) setValue(255);

                    setGreen(getValueInt());
                }
            };
        }

        if (getBlue() != null) {
            blue = new GuiSlider(
                    105,
                    width / 2 - 155, height / 6 + 66,
                    150, 20,
                    I18n.format("client.colors.blue") + " ", "",
                    0, 255, getBlue(),
                    false, true) {

                @Override
                public void updateSlider() {
                    if (getValueInt() < 0) setValue(0);
                    if (getValueInt() > 255) setValue(255);

                    setBlue(getValueInt());
                }
            };
        }

        if (getOpacity() != null) {
            opacity = new GuiSlider(
                    106,
                    width / 2 - 155, height / 6 + 90,
                    150, 20,
                    I18n.format("client.colors.opacity") + " ", "",
                    0, 255, getOpacity(),
                    false, true) {

                @Override
                public void updateSlider() {
                    if (getValueInt() < 0) setValue(0);
                    if (getValueInt() > 255) setValue(255);

                    setOpacity(getValueInt());
                }
            };
        }
    }

    @Override
    public void initGui() {
        // Setting values
        if (activated != null) {
            activated.xPosition = width / 2 - 75;
            activated.yPosition = height / 6 + 18;
        }

        if (chroma != null) {
            chroma.enabled = activated == null || isActivated();
            chroma.xPosition = width / 2 - 75;
            chroma.yPosition = height / 6 + 42;
        }

        if (red != null) {
            red.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            red.xPosition = width / 2 - 75;
            red.yPosition = height / 6 + 66;
        }

        if (green != null) {
            green.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            green.xPosition = width / 2 - 75;
            green.yPosition = height / 6 + 90;
        }

        if (blue != null) {
            blue.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            blue.xPosition = width / 2 - 75;
            blue.yPosition = height / 6 + 114;
        }

        if (opacity != null) {
            opacity.enabled = (activated == null || isActivated()) && usingOpacity;
            opacity.xPosition = width / 2 - 75;
            opacity.yPosition = height / 6 + 138;
        }

        buttonList.clear();

        // Done Button
        buttonList.add(new GuiButton(100, width / 2 - 100, height / 6 + 168, I18n.format("gui.done")));

        // Toggles
        if (activated != null) buttonList.add(activated);
        if (chroma != null) buttonList.add(chroma);

        // Sliders
        if (red != null) buttonList.add(red);
        if (green != null) buttonList.add(green);
        if (blue != null) buttonList.add(blue);
        if (opacity != null) buttonList.add(opacity);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;
        if (button.id == 100) mc.displayGuiScreen(parentGuiScreen);

        else if (button.id == 101) {
            setActivated(!isActivated());

            button.displayString = name + ": " + (isActivated() ? onI18n : offI18n);

            if (chroma != null) chroma.enabled = activated == null || isActivated();
            if (red != null) red.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            if (green != null) green.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            if (blue != null) blue.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            if (opacity != null) opacity.enabled = (activated == null || isActivated()) && usingOpacity;
        } else if (button.id == 102) {
            setChroma(!hasChroma());

            button.displayString = I18n.format("client.colors.chroma") + " " + (hasChroma() ? onI18n : offI18n);

            if (red != null) red.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            if (green != null) green.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
            if (blue != null) blue.enabled = (activated == null || isActivated()) && (chroma == null || !hasChroma());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, screenPrefix + name, width / 2, height / 6 - 6, Color.WHITE.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public abstract Boolean isActivated();

    public abstract Boolean hasChroma();

    public abstract Integer getRed();

    public abstract Integer getGreen();

    public abstract Integer getBlue();

    public abstract Integer getOpacity();

    public abstract void setActivated(Boolean activated);

    public abstract void setChroma(Boolean chroma);

    public abstract void setRed(Integer red);

    public abstract void setGreen(Integer green);

    public abstract void setBlue(Integer blue);

    public abstract void setOpacity(Integer opacity);
}