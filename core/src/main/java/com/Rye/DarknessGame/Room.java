package com.Rye.DarknessGame;

import com.badlogic.gdx.graphics.Texture;

public class Room {

    private String name;
    private int width, height;
    Texture image;
    public Room(String name, int width, int height, Texture image){
        this.name = name;
        this.width = width;
        this.height = height;
        this.image = image;
    }
    public Texture getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}
