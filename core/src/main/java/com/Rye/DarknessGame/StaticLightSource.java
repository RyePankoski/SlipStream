package com.Rye.DarknessGame;

import java.util.ArrayList;

public class StaticLightSource {


    float[] vertices;

    int x;

    int y;

    float brightness;

    public StaticLightSource(int posY, int posX,float brightness, float[] vertices) {
        initVariables(posX, posY,brightness,vertices);
    }
    public void initVariables(int x, int y, float brightness, float[] vertices) {
        this.x = x;
        this.y = y;
        this.vertices = vertices;
        this.brightness = brightness;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float[] getVertices() {
        return vertices;
    }
}
