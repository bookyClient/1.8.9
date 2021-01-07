package tk.bookyclient.bookyclient.features.keystrokes.render;

import tk.bookyclient.bookyclient.features.keystrokes.keys.CustomKey;

import java.io.Serializable;

public class ExtendedCustomKey implements Serializable {

    private final CustomKey key;
    private double xOffset;
    private double yOffset;

    public ExtendedCustomKey(CustomKey key, int xOffset, int yOffset) {
        this.key = key;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public CustomKey getKey() {
        return key;
    }

    public double getXOffset() {
        return xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }
}
