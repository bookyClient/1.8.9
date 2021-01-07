package tk.bookyclient.bookyclient.utils.gui;
// Created by booky10 in bookyClient (20:34 06.01.21)

import java.io.Serializable;

public class ClientBlockGUI implements Serializable {

    private int left, right, top, bottom;

    public ClientBlockGUI(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public boolean isMouseOver(int x, int y) {
        return x >= left && x <= right && y >= top && y <= bottom;
    }

    public ClientBlockGUI scale(double scale) {
        return new ClientBlockGUI((int) (left * scale), (int) (right * scale), (int) (top * scale), (int) (bottom * scale));
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
}
