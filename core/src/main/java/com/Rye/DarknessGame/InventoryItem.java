package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.Color;

import java.awt.*;


public class InventoryItem {
    private ItemType type;
    private float x, y;
    private int width, height;
    private com.badlogic.gdx.graphics.Color color;

    public InventoryItem(ItemType type, int width, int height) {
        this.type = type;
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;

        switch(type) {
            case WEAPON: color = com.badlogic.gdx.graphics.Color.GREEN; break;
            case POTION: color = com.badlogic.gdx.graphics.Color.BLUE; break;
            case KEY: color = com.badlogic.gdx.graphics.Color.YELLOW; break;
            default: color = Color.WHITE;
        }
    }

    public void rotate() {
        int temp = width;
        width = height;
        height = temp;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public com.badlogic.gdx.graphics.Color getColor() { return color; }
}
