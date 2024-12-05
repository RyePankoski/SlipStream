package com.Rye.DarknessGame;

public class Item {
    private String name;
    private ItemType type;
    private int width;
    private int height;

    public Item(String name, ItemType type, int width, int height) {
        this.name = name;
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
